package com.cmc.classhub.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "회원 생성 요청")
@Getter
public class CreateMemberRequest {
    @Schema(description = "회원 이름", example = "홍길동")
    private String name;

    @Schema(description = "비밀번호", example = "1234")
    private String password;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phone;
}