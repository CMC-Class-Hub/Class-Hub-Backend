package com.cmc.classhub.global.auth.dto;

public record LoginResponse(
        Long userId,
        String accessToken,
        String name
) {}
