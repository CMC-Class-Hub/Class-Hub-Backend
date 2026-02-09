package com.cmc.classhub.global.auth.controller;

import com.cmc.classhub.global.auth.dto.LoginRequest;
import com.cmc.classhub.global.auth.dto.LoginResponse;
import com.cmc.classhub.global.auth.dto.LoginStatusResponse;
import com.cmc.classhub.global.auth.dto.SignUpRequest;
import com.cmc.classhub.global.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 강사 계정을 생성합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/signup")
    public Long signUp(@RequestBody @Valid SignUpRequest req) {
        return authService.signUp(req);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/status")
    public LoginStatusResponse checkLoginStatus(@AuthenticationPrincipal Long userId) {
    
    if (userId == null) {
        return new LoginStatusResponse(false, null);
    }
    
    return new LoginStatusResponse(true, userId.toString());
}
}
