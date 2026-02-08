package com.cmc.classhub.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

import com.cmc.classhub.reservation.domain.Member;

@Schema(description = "회원 응답")
@Getter
public class MemberResponseDto {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "회원 이름", example = "홍길동")
    private String name;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phone;

    @Schema(description = "생성 일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.createdAt = member.getCreatedAt();
    }
}
