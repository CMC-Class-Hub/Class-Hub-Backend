package com.cmc.classhub.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "예약 상세 응답")
@Getter
@Builder
public class ReservationDetailResponse {
    @Schema(description = "예약 ID", example = "1")
    private Long reservationId;

    @Schema(description = "클래스 제목", example = "도자기 만들기 원데이클래스")
    private String classTitle;

    @Schema(description = "클래스 이미지 URL", example = "https://example.com/image.jpg")
    private String classImageUrl;

    @Schema(description = "클래스 장소", example = "서울시 강남구 테헤란로 123")
    private String classLocation;

    @Schema(description = "클래스 코드", example = "ABC123")
    private String classCode;

    @Schema(description = "날짜", example = "2024-01-15")
    private LocalDate date;

    @Schema(description = "시작 시간", example = "10:00:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간", example = "12:00:00")
    private LocalTime endTime;

    @Schema(description = "신청자 이름", example = "홍길동")
    private String applicantName;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "정원", example = "10")
    private Integer capacity;

    @Schema(description = "현재 신청 인원", example = "5")
    private Integer currentNum;

    @Schema(description = "세션 상태", example = "OPEN")
    private String sessionStatus;

    @Schema(description = "예약 상태", example = "CONFIRMED")
    private String reservationStatus;
}