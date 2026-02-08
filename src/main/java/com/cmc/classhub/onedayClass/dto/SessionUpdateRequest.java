package com.cmc.classhub.onedayClass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "세션 수정 요청")
public record SessionUpdateRequest(
                @Schema(description = "수업 날짜", example = "2024-01-15")
                @NotNull LocalDate date,

                @Schema(description = "시작 시간", example = "10:00:00")
                @NotNull LocalTime startTime,

                @Schema(description = "종료 시간", example = "12:00:00")
                @NotNull LocalTime endTime,

                @Schema(description = "가격", example = "50000")
                @NotNull Integer price,

                @Schema(description = "정원", example = "10")
                @NotNull Integer capacity) {
}
