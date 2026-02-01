package com.cmc.classhub.member.dto;

import com.cmc.classhub.member.domain.Member;
import lombok.Getter;

import java.time.LocalDateTime;

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
