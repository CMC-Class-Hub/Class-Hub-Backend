package com.cmc.classhub.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCancelRequest {

  private String tid;
  private Integer amount;
  private String reason;
}
