package com.cmc.classhub.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequest {

  @NotNull(message = "예약 ID는 필수입니다.")
  private Long reservationId;

  @NotNull(message = "결제 금액은 필수입니다.")
  private Integer amount;

  @NotNull(message = "주문 ID는 필수입니다.")
  private String orderId;

  // 결제 수단 정보 (선택적)
  private String method; // CARD, VBANK, etc.
  private String cardCode;
  private String cardName;
  private String cardNum;
}
