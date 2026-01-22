package com.cmc.classhub.OnedayClass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record OnedayClassCreateRequest(
        @NotBlank(message = "수업 제목은 필수입니다.")
        String title,

        @NotBlank(message = "수업 소개는 필수입니다.")
        String description,

        @NotBlank(message = "수업 장소는 필수입니다.")
        String location,

        @NotNull(message = "가격은 필수입니다.")
        Integer price,

        @NotNull(message = "보증금은 필수입니다.")
        Integer deposit,

        String material,
        String policy,

        @NotNull(message = "최소 하나 이상의 세션이 필요합니다.")
        List<SessionCreateRequest> sessions
) {
    public record SessionCreateRequest(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Integer capacity
    ) {}
}