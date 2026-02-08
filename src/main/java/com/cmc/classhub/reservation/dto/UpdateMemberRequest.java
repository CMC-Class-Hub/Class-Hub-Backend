package com.cmc.classhub.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "회원 수정 요청")
@Getter
public class UpdateMemberRequest {
    @Schema(description = "회원 이름", example = "홍길동")
    private String name;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phone;
}
