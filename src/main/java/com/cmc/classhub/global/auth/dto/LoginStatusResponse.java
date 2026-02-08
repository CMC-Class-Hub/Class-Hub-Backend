package com.cmc.classhub.global.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "로그인 상태 응답")
@Getter
@AllArgsConstructor
public class LoginStatusResponse {
    @Schema(description = "로그인 여부", example = "true")
    private boolean isLoggedIn;

    @Schema(description = "사용자명", example = "instructor@example.com")
    private String username;
}