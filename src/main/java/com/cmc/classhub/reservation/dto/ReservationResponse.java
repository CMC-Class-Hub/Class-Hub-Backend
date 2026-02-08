package com.cmc.classhub.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import com.cmc.classhub.reservation.domain.ReservationStatus;

import lombok.Builder;
import lombok.Getter;

@Schema(description = "예약 응답")
@Getter
@Builder
public class ReservationResponse {
    @Schema(description = "예약 ID", example = "1")
    private Long reservationId;

    @Schema(description = "학생 ID", example = "1")
    private Long studentId;

    @Schema(description = "신청자 이름", example = "홍길동")
    private String applicantName;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "신청 일시", example = "2024-01-15T10:30:00")
    private LocalDateTime appliedAt;

    @Schema(description = "예약 상태", example = "CONFIRMED")
    private String reservationStatus;
}