package com.cmc.classhub.instructor.dto;

public record InstructorUpdateRequest(
    String name,
    String email,
    String phoneNumber,
    String password 
) {}