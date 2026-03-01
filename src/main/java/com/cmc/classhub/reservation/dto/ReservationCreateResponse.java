package com.cmc.classhub.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "예약 생성 응답")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCreateResponse {
  @Schema(description = "예약 코드", example = "RES123456")
  private String reservationCode;
}
