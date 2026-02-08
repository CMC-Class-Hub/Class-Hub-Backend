package com.cmc.classhub.onedayClass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "원데이클래스 생성 요청")
public record OnedayClassCreateRequest(
                @Schema(description = "수업 제목", example = "도자기 만들기 원데이클래스")
                @NotBlank(message = "수업 제목은 필수입니다.") String name,

                @Schema(description = "이미지 URL 목록", example = "[\"https://example.com/image1.jpg\"]")
                List<String> images,

                @Schema(description = "수업 소개", example = "초보자도 쉽게 배울 수 있는 도자기 수업입니다.")
                @NotBlank(message = "수업 소개는 필수입니다.") String description,

                @Schema(description = "수업 장소", example = "서울시 강남구 테헤란로 123")
                @NotBlank(message = "수업 장소는 필수입니다.") String location,

                @Schema(description = "위치 상세 안내", example = "2층 공방")
                String locationDetails,

                @Schema(description = "준비물/재료", example = "편한 복장")
                String preparation,

                @Schema(description = "주의사항", example = "수업 시작 10분 전까지 도착해주세요.")
                String instructions,

                @Schema(description = "취소 규정", example = "수업 3일 전까지 전액 환불 가능")
                String cancellationPolicy,

                @Schema(description = "주차 안내", example = "건물 내 주차 가능 (2시간 무료)")
                String parkingInfo
) {
}