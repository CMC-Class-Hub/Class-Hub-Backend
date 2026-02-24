package com.cmc.classhub.settlement.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementStatus {
  READY("정산 대기"),
  PAID("지급 완료"),
  CANCELLED("정산 취소");

  private final String description;
}
