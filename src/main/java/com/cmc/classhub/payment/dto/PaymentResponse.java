package com.cmc.classhub.payment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import com.cmc.classhub.payment.domain.Payment;
import com.cmc.classhub.payment.domain.PaymentStatus;

@Getter
@Builder
public class PaymentResponse {

  private Long id;
  private Long reservationId;
  private String reservationCode;
  private String tid;
  private String orderId;
  private Integer amount;
  private PaymentStatus status;
  private String method;
  private String cardCode;
  private String cardName;
  private String cardNum;
  private String resultCode;
  private String resultMsg;
  private LocalDateTime approvedAt;
  private LocalDateTime cancelledAt;
  private LocalDateTime createdAt;

  public static PaymentResponse from(Payment payment) {
    return PaymentResponse.builder()
        .id(payment.getId())
        .reservationId(payment.getReservation().getId())
        .reservationCode(payment.getReservation().getReservationCode())
        .tid(payment.getTid())
        .orderId(payment.getOrderId())
        .amount(payment.getAmount())
        .status(payment.getStatus())
        .method(payment.getMethod())
        .cardCode(payment.getCardCode())
        .cardName(payment.getCardName())
        .cardNum(payment.getCardNum())
        .resultCode(payment.getResultCode())
        .resultMsg(payment.getResultMsg())
        .approvedAt(payment.getApprovedAt())
        .cancelledAt(payment.getCancelledAt())
        .createdAt(payment.getCreatedAt())
        .build();
  }
}
