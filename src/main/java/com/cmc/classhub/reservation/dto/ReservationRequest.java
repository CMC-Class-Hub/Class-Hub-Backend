package com.cmc.classhub.reservation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReservationRequest {

    @NotNull(message = "신청할 세션 정보는 필수입니다.")
    private Long sessionId;

    @NotBlank(message = "신청자 성함은 필수입니다.")
    private String applicantName;

    @NotBlank(message = "연락처는 필수 입력 사항입니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다. (예: 010-1234-5678)")
    private String phoneNumber;

    // 무통장 입금 시 입금자명이 다를 수 있으므로 추가
    //private String depositorName;
}