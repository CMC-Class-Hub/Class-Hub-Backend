package com.cmc.classhub.reservation.dto;

import lombok.Getter;

@Getter
public class CreateMemberRequest {
    private String name;
    private String password;
    private String phone;
}