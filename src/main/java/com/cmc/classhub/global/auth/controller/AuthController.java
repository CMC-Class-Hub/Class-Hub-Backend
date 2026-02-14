package com.cmc.classhub.global.auth.controller;

import com.cmc.classhub.global.auth.dto.*;
import com.cmc.classhub.global.auth.service.AuthService;
import com.cmc.classhub.global.auth.jwt.JwtCookieManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtCookieManager jwtCookieManager;

    @Operation(summary = "회원가입", description = "새로운 강사 계정을 생성합니다")
    @PostMapping("/signup")
    public Long signUp(@RequestBody @Valid SignUpRequest req) {
        return authService.signUp(req);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req, HttpServletResponse response) {
        LoginResultDto result = authService.login(req);

        jwtCookieManager.addAccessTokenCookie(response, result.tokenDto().accessToken());
        jwtCookieManager.addRefreshTokenCookie(response, result.tokenDto().refreshToken());

        return ResponseEntity.ok(result.loginResponse());
    }

    @Operation(summary = "로그아웃", description = "쿠키를 제거하여 로그아웃합니다")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        jwtCookieManager.clearTokenCookies(response);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 엑세스 토큰을 갱신합니다")
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        refreshToken = Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        TokenDto tokenDto = authService.refresh(refreshToken);
        jwtCookieManager.addAccessTokenCookie(response, tokenDto.accessToken());
        jwtCookieManager.addRefreshTokenCookie(response, tokenDto.refreshToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public LoginStatusResponse checkLoginStatus(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            return new LoginStatusResponse(false, null);
        }
        return new LoginStatusResponse(true, userId.toString());
    }

}
