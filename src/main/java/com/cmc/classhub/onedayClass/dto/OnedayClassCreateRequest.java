package com.cmc.classhub.onedayClass.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record OnedayClassCreateRequest(
                @NotBlank(message = "수업 제목은 필수입니다.") String name,

                List<String> images,

                @NotBlank(message = "수업 소개는 필수입니다.") String description,

                @NotBlank(message = "수업 장소는 필수입니다.") String location,

                String locationDetails, // 위치 안내 추가

                String preparation, // 준비물/재료
                String instructions, // 주의사항
                String cancellationPolicy, // 규정
                String parkingInfo // 주차안내
) {
}