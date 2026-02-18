package com.cmc.classhub.payment.controller;

import com.cmc.classhub.payment.dto.PaymentCancelRequest;
import com.cmc.classhub.payment.dto.PaymentRequest;
import com.cmc.classhub.payment.dto.PaymentResponse;
import com.cmc.classhub.payment.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @Value("${frontend.url:http://localhost:3000}")
  private String frontendUrl;

  /**
   * 결제 생성 (결제 준비 단계)
   * POST /api/payments
   */
  @PostMapping
  public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
    PaymentResponse response = paymentService.createPayment(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * 결제 승인 처리 (Nicepay에서 리다이렉트 후 호출)
   * GET /api/payments/approve
   */
  @RequestMapping("/clientAuth")
  public ResponseEntity<Void> approvePayment(HttpServletRequest request) {
    System.out.println("=== Nicepay Callback Recieved ===");

    // authResultCode가 없으면 resultCode를 확인
    String resultCode = request.getParameter("authResultCode") != null ? request.getParameter("authResultCode")
        : request.getParameter("resultCode");
    String resultMsg = request.getParameter("authResultMsg") != null ? request.getParameter("authResultMsg")
        : request.getParameter("resultMsg");

    String tid = request.getParameter("tid");
    String orderId = request.getParameter("orderId");
    String amount = request.getParameter("amount");
    String payMethod = request.getParameter("payMethod");
    String cardCode = request.getParameter("cardCode");
    String cardName = request.getParameter("cardName");
    String cardNum = request.getParameter("cardNum");

    // 로그 출력
    logRequestParameters(request);

    String redirectUrl;
    if (orderId == null || orderId.isEmpty()) {
      redirectUrl = frontendUrl + "/payment/result?success=false&resultMsg="
          + URLEncoder.encode("orderId가 없습니다.", StandardCharsets.UTF_8);
    } else {
      // 결제 승인 처리
      PaymentResponse paymentResponse = paymentService.approvePayment(
          orderId, tid, resultCode, resultMsg,
          amount, payMethod, cardCode, cardName, cardNum);

      if ("0000".equals(resultCode)) {
        redirectUrl = String.format("%s/payment/result?success=true&reservationCode=%s&resultCode=0000",
            frontendUrl, paymentResponse.getReservationCode());
      } else {
        redirectUrl = String.format("%s/payment/result?success=false&reservationCode=%s&resultMsg=%s",
            frontendUrl, paymentResponse.getReservationCode(),
            URLEncoder.encode(resultMsg != null ? resultMsg : "결제 실패", StandardCharsets.UTF_8));
      }
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(redirectUrl));
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }

  /**
   * 결제 취소
   * POST /api/payments/cancel
   */
  @PostMapping("/cancel")
  public ResponseEntity<Map<String, Object>> cancelPayment(@RequestBody PaymentCancelRequest request) {
    try {
      PaymentResponse response = paymentService.cancelPayment(request);

      Map<String, Object> result = new HashMap<>();
      result.put("success", true);
      result.put("resultMsg", "결제 취소 성공");
      result.put("payment", response);

      return ResponseEntity.ok(result);
    } catch (Exception e) {
      Map<String, Object> result = new HashMap<>();
      result.put("success", false);
      result.put("resultMsg", e.getMessage());

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
  }

  /**
   * Webhook 처리 (Nicepay에서 결제 상태 변경 시 호출)
   * POST /api/payments/hook
   */
  @PostMapping("/hook")
  public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> hookMap) {
    String resultCode = hookMap.get("resultCode").toString();
    String tid = hookMap.get("tid") != null ? hookMap.get("tid").toString() : null;
    String orderId = hookMap.get("orderId") != null ? hookMap.get("orderId").toString() : null;

    System.out.println("Webhook received: " + hookMap);

    try {
      if ("0000".equals(resultCode) && tid != null && orderId != null) {
        // 결제 성공 처리
        paymentService.approvePayment(
            orderId,
            tid,
            resultCode,
            hookMap.get("resultMsg") != null ? hookMap.get("resultMsg").toString() : "",
            hookMap.get("amount") != null ? hookMap.get("amount").toString() : null,
            hookMap.get("payMethod") != null ? hookMap.get("payMethod").toString() : null,
            hookMap.get("cardCode") != null ? hookMap.get("cardCode").toString() : null,
            hookMap.get("cardName") != null ? hookMap.get("cardName").toString() : null,
            hookMap.get("cardNum") != null ? hookMap.get("cardNum").toString() : null);
        return ResponseEntity.ok("ok");
      }
    } catch (Exception e) {
      System.err.println("Webhook processing error: " + e.getMessage());
    }

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
  }

  /**
   * TID로 결제 조회
   * GET /api/payments/tid/{tid}
   */
  @GetMapping("/tid/{tid}")
  public ResponseEntity<PaymentResponse> getPaymentByTid(@PathVariable String tid) {
    PaymentResponse response = paymentService.getPaymentByTid(tid);
    return ResponseEntity.ok(response);
  }

  /**
   * Order ID로 결제 조회
   * GET /api/payments/order/{orderId}
   */
  @GetMapping("/order/{orderId}")
  public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable String orderId) {
    PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
    return ResponseEntity.ok(response);
  }

  /**
   * 예약 ID로 결제 조회
   * GET /api/payments/reservation/{reservationId}
   */
  @GetMapping("/reservation/{reservationId}")
  public ResponseEntity<PaymentResponse> getPaymentByReservationId(@PathVariable Long reservationId) {
    PaymentResponse response = paymentService.getPaymentByReservationId(reservationId);
    return ResponseEntity.ok(response);
  }

  /**
   * Request 파라미터 로그 출력 (디버깅용)
   */
  private void logRequestParameters(HttpServletRequest request) {
    Enumeration<String> params = request.getParameterNames();
    System.out.println("=== Payment Request Parameters ===");
    while (params.hasMoreElements()) {
      String paramName = params.nextElement();
      System.out.println(paramName + " : " + request.getParameter(paramName));
    }
    System.out.println("==================================");
  }
}
