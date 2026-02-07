package com.cmc.classhub.onedayClass.dto;

import java.util.List;

import com.cmc.classhub.onedayClass.domain.OnedayClass;

import com.cmc.classhub.onedayClass.domain.CLASS_IMAGE;

public record OnedayClassResponse(
        
                Long id,
                String name,
                List<String> imageUrls,
                String description,
                String location,
                String locationDetails,
                String preparation,
                String parkingInfo,
                String instructions,
                String cancellationPolicy,
                String classCode,
                Long instructorId,
                String linkShareStatus
        
        ) {
        public static OnedayClassResponse from(OnedayClass onedayClass) {
                List<String> imageUrls = onedayClass.getImages().stream()
                                .map(CLASS_IMAGE::getImageUrl)
                                .toList();

                return new OnedayClassResponse(
                                onedayClass.getId(),
                                onedayClass.getTitle(),
                                imageUrls,
                                onedayClass.getDescription(),
                                onedayClass.getLocation(),
                                onedayClass.getLocationDescription(),
                                onedayClass.getMaterial(),
                                onedayClass.getParkingInfo(),
                                onedayClass.getGuidelines(),
                                onedayClass.getPolicy(),
                                onedayClass.getClassCode(),
                                onedayClass.getInstructorId(),
                                onedayClass.getLinkShareStatus().toString());
        }
}
