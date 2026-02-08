package com.cmc.classhub.onedayClass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.cmc.classhub.onedayClass.domain.OnedayClass;
import java.util.List;

@Schema(description = "원데이클래스 상세 응답")
public record OnedayClassDetailResponse(
        @Schema(description = "클래스 ID", example = "1")
        Long id,

        @Schema(description = "클래스 코드", example = "ABC123")
        String classCode,

        @Schema(description = "수업 제목", example = "도자기 만들기 원데이클래스")
        String title,

        @Schema(description = "이미지 URL 목록")
        List<String> imageUrls,

        @Schema(description = "수업 소개", example = "초보자도 쉽게 배울 수 있는 도자기 수업입니다.")
        String description,

        @Schema(description = "수업 장소", example = "서울시 강남구 테헤란로 123")
        String location,

        @Schema(description = "위치 상세 안내", example = "2층 공방")
        String locationDescription,

        @Schema(description = "준비물/재료", example = "편한 복장")
        String material,

        @Schema(description = "주차 안내", example = "건물 내 주차 가능")
        String parkingInfo,

        @Schema(description = "주의사항", example = "수업 시작 10분 전까지 도착해주세요.")
        String guidelines,

        @Schema(description = "취소 규정", example = "수업 3일 전까지 전액 환불 가능")
        String policy,

        @Schema(description = "링크 공유 상태", example = "ACTIVE")
        String linkShareStatus,

        @Schema(description = "세션 목록")
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