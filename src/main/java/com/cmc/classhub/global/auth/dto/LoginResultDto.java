package com.cmc.classhub.global.auth.dto;

public record LoginResultDto(
    LoginResponse loginResponse,
    TokenDto tokenDto) {
}
