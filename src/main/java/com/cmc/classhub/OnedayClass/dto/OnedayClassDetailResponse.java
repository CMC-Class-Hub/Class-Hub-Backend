package com.cmc.classhub.OnedayClass.dto;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import java.util.List;

public record OnedayClassDetailResponse(
        String shareCode,
        String title,
        String description,
        String location,
        Integer price,
        String material,
        String policy,
        List<SessionResponse> sessions
) {
    public static OnedayClassDetailResponse from(OnedayClass entity) {
        return new OnedayClassDetailResponse(
                entity.getShareCode(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getPrice(),
                entity.getMaterial(),
                entity.getPolicy(),
                entity.getSessions().stream()
                        .filter(s -> !s.isDeleted()) // 삭제되지 않은 세션만
                        .map(SessionResponse::from)
                        .toList()
        );
    }
}