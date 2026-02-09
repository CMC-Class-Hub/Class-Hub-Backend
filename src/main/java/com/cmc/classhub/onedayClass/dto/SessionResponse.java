package com.cmc.classhub.onedayClass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.cmc.classhub.onedayClass.domain.Session;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "세션 응답")
public record SessionResponse(
        @Schema(description = "세션 ID", example = "1")
        Long id,

        @Schema(description = "수업 날짜", example = "2024-01-15")
        LocalDate date,

        @Schema(description = "시작 시간", example = "10:00:00")
        LocalTime startTime,

        @Schema(description = "종료 시간", example = "12:00:00")
        LocalTime endTime,

        @Schema(description = "현재 신청 인원", example = "5")
        int currentNum,

        @Schema(description = "정원", example = "10")
        int capacity,

        @Schema(description = "가격", example = "50000")
        int price,

        @Schema(description = "세션 상태", example = "OPEN")
        String status) {
    public static SessionResponse from(Session session) {
        return new SessionResponse(
                session.getId(),
                session.getDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getCurrentNum(),
                session.getCapacity(),
                session.getPrice(),
                session.getStatus().name());
    }
}
