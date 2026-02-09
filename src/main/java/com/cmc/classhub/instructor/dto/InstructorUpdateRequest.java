package com.cmc.classhub.instructor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강사 정보 수정 요청")
public record InstructorUpdateRequest(
    @Schema(description = "강사 이름", example = "홍길동")
    String name,

    @Schema(description = "이메일", example = "instructor@example.com")
    String email,

    @Schema(description = "전화번호", example = "010-1234-5678")
    String phoneNumber,

    @Schema(description = "비밀번호", example = "newpassword123")
    String password
) {}