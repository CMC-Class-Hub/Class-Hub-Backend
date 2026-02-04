package com.cmc.classhub.reservation.dto;

import lombok.Getter;

import java.time.LocalDateTime;

import com.cmc.classhub.reservation.domain.Member;

@Getter
public class MemberResponseDto {

    private Long id;
    private String name;
    private String phone;
    private LocalDateTime createdAt;

    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.createdAt = member.getCreatedAt();
    }
}
