package com.cmc.classhub.settlement.dto;

import com.cmc.classhub.settlement.domain.Settlement;
import com.cmc.classhub.settlement.domain.SettlementStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SettlementResponse {
  private Long id;
  private Long instructorId;
  private Long reservationId;
  private Integer amount;
  private SettlementStatus status;
  private LocalDateTime paidAt;
  private LocalDateTime createdAt;

  public static SettlementResponse from(Settlement settlement) {
    return SettlementResponse.builder()
        .id(settlement.getId())
        .instructorId(settlement.getInstructorId())
        .reservationId(settlement.getReservationId())
        .amount(settlement.getAmount())
        .status(settlement.getStatus())
        .paidAt(settlement.getPaidAt())
        .createdAt(settlement.getCreatedAt())
        .build();
  }
}
