package com.cmc.classhub.admin.dto;

import java.time.LocalDateTime;

public record InstructorAdminResponse(
    String name,
    String email,
    LocalDateTime createdAt,
    long onedayClassCount,
    long sessionCount,
    long reservationCount) {
}
