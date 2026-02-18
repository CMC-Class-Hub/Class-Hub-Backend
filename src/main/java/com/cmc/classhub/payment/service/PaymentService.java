package com.cmc.classhub.payment.service;

import com.cmc.classhub.payment.domain.Payment;
import com.cmc.classhub.payment.domain.PaymentStatus;
import com.cmc.classhub.payment.dto.PaymentCancelRequest;
import com.cmc.classhub.payment.dto.PaymentRequest;
import com.cmc.classhub.payment.dto.PaymentResponse;
import com.cmc.classhub.payment.repository.PaymentRepository;
import com.cmc.classhub.reservation.domain.*;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import com.cmc.classhub.reservation.service.ReservationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final ReservationRepository reservationRepository;
  private final ReservationService reservationService;
  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${nicepay.client-id}")
  private String CLIENT_ID;

  @Value("${nicepay.secret-key}")
  private String SECRET_KEY;

  @Value("${nicepay.api-url:https://sandbox-api.nicepay.co.kr}")
  private String API_URL;

  /**
   * 결제 생성 (결제 준비 단계)
   */
  @Transactional
  public PaymentResponse createPayment(PaymentRequest request) {
    Reservation reservation = reservationRepository.findById(request.getReservationId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

    // 이미 결제가 완료된 예약인지 확인
    Long reservationId = Objects.requireNonNull(reservation.getId());
    paymentRepository.findByReservationId(reservationId)
        .ifPresent(payment -> {
          if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("이미 결제가 완료된 예약입니다.");
          }
        });

    Payment payment = Payment.builder()
        .reservation(reservation)
        .orderId(request.getOrderId())
        .amount(request.getAmount())
        .status(PaymentStatus.PENDING)
        .method(request.getMethod())
        .cardCode(request.getCardCode())
        .cardName(request.getCardName())
        .cardNum(request.getCardNum())
        .build();

    Payment savedPayment = Objects.requireNonNull(paymentRepository.save(payment));
    return PaymentResponse.from(savedPayment);
  }

  /**
   * 결제 승인 처리 (Nicepay 결제 완료 후 호출)
   */
  @Transactional
  public PaymentResponse approvePayment(String orderId, String tid, String resultCode, String resultMsg,
      String amount, String payMethod, String cardCode, String cardName, String cardNum) {
    // 락을 사용하여 중복 처리 방지 (Redirect와 Webhook 동시 호출 대응)
    Payment payment = paymentRepository.findByOrderIdWithLock(orderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

    // 이미 처리된 결제라면 보관된 결과 반환
    if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.FAILED) {
      return PaymentResponse.from(payment);
    }

    if ("0000".equals(resultCode)) {
      // 금액 검증 (선택적이지만 권장)
      if (amount != null && !amount.isEmpty()) {
        try {
          int requestAmount = Integer.parseInt(amount);
          if (requestAmount != payment.getAmount()) {
            payment.fail("AMOUNT_MISMATCH", "결제 금액 불일치: 요청(" + requestAmount + "), DB(" + payment.getAmount() + ")");
            // 예약 실패 처리 (인원 복구 등)
            reservationService.failReservation(payment.getReservation().getReservationCode());
            return PaymentResponse.from(payment);
          }
        } catch (NumberFormatException e) {
          // 숫자 형식이 아니면 무시하거나 에러 처리
        }
      }

      // 나이스페이 결제 방식에 따라 결제와 승인이 한 번에 완료되므로 바로 승인 처리
      payment.approve(tid, resultCode, resultMsg);
      payment.updatePaymentInfo(payMethod, cardCode, cardName, cardNum);

      // 예약 확정 처리
      reservationService.completeReservation(payment.getReservation().getReservationCode());

    } else {
      // 결제창 이탈 또는 사용자 취소 등
      payment.fail(resultCode, resultMsg);

      // 예약 실패 처리 (인원 복구 등)
      reservationService.failReservation(payment.getReservation().getReservationCode());
    }

    return PaymentResponse.from(payment);
  }

  /**
   * 결제 취소
   */
  @Transactional
  public PaymentResponse cancelPayment(PaymentCancelRequest request) throws Exception {
    Payment payment = paymentRepository.findByTid(request.getTid())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

    if (payment.getStatus() != PaymentStatus.COMPLETED) {
      throw new IllegalStateException("완료된 결제만 취소할 수 있습니다.");
    }

    // Nicepay API 호출하여 결제 취소
    HttpHeaders headers = new HttpHeaders();
    String auth = CLIENT_ID + ":" + SECRET_KEY;
    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    headers.set("Authorization", "Basic " + encodedAuth);
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> cancelMap = new HashMap<>();
    cancelMap.put("amount", request.getAmount() != null ? request.getAmount() : payment.getAmount());
    cancelMap.put("reason", request.getReason() != null ? request.getReason() : "사용자 요청");
    cancelMap.put("orderId", UUID.randomUUID().toString());

    HttpEntity<String> httpRequest = new HttpEntity<>(objectMapper.writeValueAsString(cancelMap), headers);

    ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
        API_URL + "/v1/payments/" + request.getTid() + "/cancel",
        httpRequest,
        JsonNode.class);

    JsonNode responseNode = responseEntity.getBody();
    if (responseNode == null) {
      throw new RuntimeException("결제 취소 API 응답이 없습니다.");
    }
    String resultCode = responseNode.get("resultCode").asText();
    String resultMsg = responseNode.get("resultMsg").asText();

    if ("0000".equals(resultCode)) {
      // 취소 성공
      payment.cancel(resultMsg);

      // 연관된 예약도 취소 처리 (결제 취소 시 인원 복구가 필요하므로 failReservation 로직 재사용 가능)
      // 또는 별도의 취소 로직 호출
      reservationService.failReservation(payment.getReservation().getReservationCode());
    } else {
      // 취소 실패
      throw new RuntimeException("결제 취소 실패: " + resultMsg);
    }

    return PaymentResponse.from(payment);
  }

  /**
   * 결제 조회 (tid로)
   */
  public PaymentResponse getPaymentByTid(String tid) {
    Payment payment = paymentRepository.findByTid(tid)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));
    return PaymentResponse.from(payment);
  }

  /**
   * 결제 조회 (orderId로)
   */
  public PaymentResponse getPaymentByOrderId(String orderId) {
    Payment payment = paymentRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));
    return PaymentResponse.from(payment);
  }

  /**
   * 예약 ID로 결제 조회
   */
  public PaymentResponse getPaymentByReservationId(Long reservationId) {
    Payment payment = paymentRepository.findByReservationId(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("해당 예약의 결제 내역이 없습니다."));
    return PaymentResponse.from(payment);
  }
}
