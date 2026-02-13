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
        Cookie accessCookie = new Cookie("accessToken", tokenDto.accessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(isSecure);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) accessExp);

        Cookie refreshCookie = new Cookie("refreshToken", tokenDto.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(isSecure);
        refreshCookie.setPath("/api/auth");
        refreshCookie.setMaxAge((int) refreshExp);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    private void clearTokenCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(isSecure);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(isSecure);
        refreshCookie.setPath("/api/auth");
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}
