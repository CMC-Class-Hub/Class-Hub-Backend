package com.cmc.classhub.instructor.dto;

public record InstructorLoginRequest(
        String businessName,
        String name,
        String phoneNumber
) {}