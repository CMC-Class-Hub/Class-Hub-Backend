package com.cmc.classhub.OnedayClass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record OnedayClassCreateRequest(
        @NotBlank(message = "수업 제목은 필수입니다.")
        String title,

        String imageUrl,

        @NotBlank(message = "수업 소개는 필수입니다.")
        String description,

        @NotBlank(message = "수업 장소는 필수입니다.")
        String location,

        String locationDescription, // 위치 안내 추가

        @NotNull(message = "가격은 필수입니다.")
        Integer price,

        String material,     // 준비물
        String parkingInfo,  // 주차안내 추가
        String guidelines,   // 주의사항 추가
        String policy,       // 취소 규정

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