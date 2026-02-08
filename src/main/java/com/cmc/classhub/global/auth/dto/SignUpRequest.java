package com.cmc.classhub.global.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원가입 요청")
public record SignUpRequest(
        @Schema(description = "강사 이름", example = "홍길동")
        @NotBlank String name,

        @Schema(description = "이메일", example = "instructor@example.com")
        @Email @NotBlank String email,

        @Schema(description = "전화번호", example = "010-1234-5678")
        @NotBlank String phoneNumber,

        @Schema(description = "비밀번호", example = "password123")
        @NotBlank String password
) {}
