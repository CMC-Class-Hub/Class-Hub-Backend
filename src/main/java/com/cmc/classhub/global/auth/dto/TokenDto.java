package com.cmc.classhub.global.auth.dto;

public record TokenDto(
    String accessToken,
    String refreshToken) {
}
