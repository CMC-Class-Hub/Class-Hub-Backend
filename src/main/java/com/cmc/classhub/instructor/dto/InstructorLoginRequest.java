package com.cmc.classhub.instructor.dto;

public record InstructorLoginRequest(
        String name,
        String phoneNumber
) {}