package com.cmc.classhub.global.auth.controller;

import com.cmc.classhub.global.auth.dto.LoginRequest;
import com.cmc.classhub.global.auth.dto.LoginResponse;
import com.cmc.classhub.global.auth.dto.LoginStatusResponse;
import com.cmc.classhub.global.auth.dto.SignUpRequest;
import com.cmc.classhub.global.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public Long signUp(@RequestBody @Valid SignUpRequest req) {
        return authService.signUp(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/status")
    public LoginStatusResponse checkLoginStatus(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return new LoginStatusResponse(false, null);
        }
        
        String username = userDetails.getUsername();
        // demo-instructor는 실제 로그인으로 간주하지 않음
        boolean isLoggedIn = !"demo-instructor".equals(username);
        
        return new LoginStatusResponse(isLoggedIn, isLoggedIn ? username : null);
    }
}
