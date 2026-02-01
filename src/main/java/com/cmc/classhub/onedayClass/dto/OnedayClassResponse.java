package com.cmc.classhub.onedayClass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.cmc.classhub.onedayClass.domain.OnedayClass;

public record OnedayClassResponse(
    Long id,
    String name,
    String thumbnailImageUrl,
    String description,
    String location,
    String locationDescription,
    Integer price,
    String preparation,
    String parkingInfo,
    String guidelines,
    String policy,
    String classCode,
    Long instructorId
) {
    public static OnedayClassResponse from(OnedayClass onedayClass) {
        String thumbnail = onedayClass.getImages().isEmpty()
            ? null
            : onedayClass.getImages().get(0).getImageUrl();

        return new OnedayClassResponse(
            onedayClass.getId(),
            onedayClass.getTitle(),
            thumbnail,
            onedayClass.getDescription(),
            onedayClass.getLocation(),
            onedayClass.getLocationDescription(),
            onedayClass.getPrice(),
            onedayClass.getMaterial(),
            onedayClass.getParkingInfo(),
            onedayClass.getGuidelines(),
            onedayClass.getPolicy(),
            onedayClass.getClassCode(),
            onedayClass.getInstructorId()
        );
    }
}

