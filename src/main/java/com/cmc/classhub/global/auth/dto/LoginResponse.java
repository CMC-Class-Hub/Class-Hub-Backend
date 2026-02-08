package com.cmc.classhub.global.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "사용자 이름", example = "홍길동")
        String name,

        @Schema(description = "전화번호", example = "010-1234-5678")
        String PhoneNumber
) {}
