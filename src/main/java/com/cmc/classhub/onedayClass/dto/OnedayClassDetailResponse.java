package com.cmc.classhub.onedayClass.dto;

import com.cmc.classhub.onedayClass.domain.OnedayClass;
import java.util.List;

public record OnedayClassDetailResponse(
        Long id,
        String classCode,
        String title,
        List<String> imageUrls,
        String description,
        String location,
        String locationDescription,
        String material,
        String parkingInfo,
        String guidelines,
        String policy,
        String linkShareStatus,
        List<SessionResponse> sessions) {
    public static OnedayClassDetailResponse from(OnedayClass entity) {
        return new OnedayClassDetailResponse(
                entity.getId(),
                entity.getClassCode(),
                entity.getTitle(),
                entity.getImages()
                        .stream()
                        .map(img -> img.getImageUrl())
                        .toList(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getLocationDescription(),
                entity.getMaterial(),
                entity.getParkingInfo(),
                entity.getGuidelines(),
                entity.getPolicy(),
                entity.getLinkShareStatus().toString(),
                entity.getSessions().stream()
                        .filter(s -> !s.isDeleted())
                        .map(SessionResponse::from)
                        .toList());
    }
}