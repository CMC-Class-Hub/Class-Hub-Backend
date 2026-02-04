package com.cmc.classhub.onedayClass.dto;

import com.cmc.classhub.onedayClass.domain.OnedayClass;
import com.cmc.classhub.onedayClass.domain.SessionStatus;
import java.util.List;

public record OnedayClassDetailResponse(
    Long id,
    String classCode,
    String title,
    List<String> imageUrls,
    String description,
    String location,
    String locationDescription,
    Integer price,
    String material,
    String parkingInfo,
    String guidelines,
    String policy,
    List<SessionResponse> sessions
) {
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
            entity.getPrice(),
            entity.getMaterial(),
            entity.getParkingInfo(),
            entity.getGuidelines(),
            entity.getPolicy(),
            entity.getSessions().stream()
                .filter(s -> s.getStatus() != SessionStatus.DELETED)
                .map(SessionResponse::from)
                .toList()
        );
    }
}