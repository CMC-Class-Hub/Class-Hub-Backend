package com.cmc.classhub.global.auth.controller;

import com.cmc.classhub.global.auth.dto.*;
import com.cmc.classhub.global.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${security.jwt.cookie.secure}")
    private boolean isSecure;

    @Value("${security.jwt.access-exp-seconds}")
    private long accessExp;

    @Value("${security.jwt.refresh-exp-seconds}")
    private long refreshExp;

    @Operation(summary = "회원가입", description = "새로운 강사 계정을 생성합니다")
    @PostMapping("/signup")
    public Long signUp(@RequestBody @Valid SignUpRequest req) {
        return authService.signUp(req);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req, HttpServletResponse response) {
        LoginResultDto result = authService.login(req);

        setTokenCookies(response, result.tokenDto());

        return ResponseEntity.ok(result.loginResponse());
    }

    @Operation(summary = "로그아웃", description = "쿠키를 제거하여 로그아웃합니다")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        clearTokenCookies(response);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 엑세스 토큰을 갱신합니다")
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            TokenDto tokenDto = authService.refresh(refreshToken);
            setTokenCookies(response, tokenDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            clearTokenCookies(response);
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/status")
    public LoginStatusResponse checkLoginStatus(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            return new LoginStatusResponse(false, null);
        }
        return new LoginStatusResponse(true, userId.toString());
    }

    private void setTokenCookies(HttpServletResponse response, TokenDto tokenDto) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokenDto.accessToken())
                .httpOnly(true)
                .secure(isSecure)          // 운영 HTTPS면 true
                .sameSite("None")          // ⭐ 핵심
                .path("/")
                .maxAge(accessExp)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenDto.refreshToken())
                .httpOnly(true)
                .secure(isSecure)
                .sameSite("None")          // ⭐ 핵심
                .path("/api/auth")         // 지금처럼 제한해도 OK
                .maxAge(refreshExp)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void clearTokenCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite("None")
                .path("/api/auth")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
