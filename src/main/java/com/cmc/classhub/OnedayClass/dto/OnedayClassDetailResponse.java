package com.cmc.classhub.OnedayClass.dto;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.domain.SessionStatus;
import java.util.List;

public record OnedayClassDetailResponse(
        Long id,
        String shareCode,
        String title,
        String imageUrl,
        String description,
        String location,
        String locationDescription, // 추가
        Integer price,
        String material,
        String parkingInfo, // 추가
        String guidelines,  // 추가
        String policy,
        List<SessionResponse> sessions
) {
    public static OnedayClassDetailResponse from(OnedayClass entity) {
        return new OnedayClassDetailResponse(
                entity.getId(),
                entity.getShareCode(),
                entity.getTitle(),
                entity.getImageUrl(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getLocationDescription(), // 매핑
                entity.getPrice(),
                entity.getMaterial(),
                entity.getParkingInfo(), // 매핑
                entity.getGuidelines(),  // 매핑
                entity.getPolicy(),
                entity.getSessions().stream()
                        .filter(s -> s.getStatus() != SessionStatus.DELETED)
                        .map(SessionResponse::from)
                        .toList()
        );
    }
}