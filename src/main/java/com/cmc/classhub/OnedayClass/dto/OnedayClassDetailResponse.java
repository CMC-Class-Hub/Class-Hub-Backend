package com.cmc.classhub.OnedayClass.dto;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.domain.SessionStatus;
import java.util.List;

public record OnedayClassDetailResponse(
        Long id, // 프론트엔드 예약 요청 시 필요하므로 추가
        String shareCode,
        String title,
        String description,
        String location,
        Integer price,
        Integer deposit, // 누락된 보증금 필드 추가
        String material,
        String policy,
        List<SessionResponse> sessions
) {
    public static OnedayClassDetailResponse from(OnedayClass entity) {
        return new OnedayClassDetailResponse(
                entity.getId(),
                entity.getShareCode(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getPrice(),
                entity.getDeposit(),
                entity.getMaterial(),
                entity.getPolicy(),
                entity.getSessions().stream()
                        // 삭제 상태가 아닌 세션만 필터링 (SessionStatus 활용)
                        .filter(s -> s.getStatus() != SessionStatus.DELETED)
                        .map(SessionResponse::from)
                        .toList()
        );
    }
}