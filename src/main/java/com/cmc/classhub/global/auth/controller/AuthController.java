package com.cmc.classhub.global.auth.controller;

import com.cmc.classhub.global.auth.dto.LoginRequest;
import com.cmc.classhub.global.auth.dto.LoginResponse;
import com.cmc.classhub.global.auth.dto.SignUpRequest;
import com.cmc.classhub.global.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
