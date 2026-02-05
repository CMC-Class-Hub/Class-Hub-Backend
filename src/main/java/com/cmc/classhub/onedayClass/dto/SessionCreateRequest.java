package com.cmc.classhub.onedayClass.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record SessionCreateRequest(
                @NotNull Long templateId, // templateId
                @NotNull LocalDate date,
                @NotNull LocalTime startTime,
                @NotNull LocalTime endTime,
                @NotNull Integer price,
                @NotNull Integer capacity) {
}
