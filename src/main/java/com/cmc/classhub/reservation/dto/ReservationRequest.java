package com.cmc.classhub.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "예약 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReservationRequest {

    @Schema(description = "세션 ID", example = "1")
    @NotNull(message = "신청할 세션 정보는 필수입니다.")
    private Long sessionId;

    @Schema(description = "신청자 이름", example = "홍길동")
    @NotBlank(message = "신청자 성함은 필수입니다.")
    private String applicantName;

    @Schema(description = "연락처", example = "010-1234-5678")
    @NotBlank(message = "연락처는 필수 입력 사항입니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다. (예: 010-1234-5678)")
    private String phoneNumber;

    @Schema(description = "예약 비밀번호", example = "1234")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
