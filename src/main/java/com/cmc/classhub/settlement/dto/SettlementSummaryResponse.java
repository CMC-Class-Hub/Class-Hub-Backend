package com.cmc.classhub.settlement.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SettlementSummaryResponse {
  private List<SettlementResponse> settlements;
  private Long totalPaidAmount;
  private Long totalReadyAmount;

  public static SettlementSummaryResponse of(List<SettlementResponse> settlements, Long totalPaidAmount,
      Long totalReadyAmount) {
    return SettlementSummaryResponse.builder()
        .settlements(settlements)
        .totalPaidAmount(totalPaidAmount)
        .totalReadyAmount(totalReadyAmount)
        .build();
  }
}
