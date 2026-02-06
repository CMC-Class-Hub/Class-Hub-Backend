package com.cmc.classhub.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginStatusResponse {
    private boolean isLoggedIn;
    private String username;
}