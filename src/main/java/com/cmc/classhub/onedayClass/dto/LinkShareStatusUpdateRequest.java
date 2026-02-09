package com.cmc.classhub.onedayClass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.cmc.classhub.onedayClass.domain.LinkShareStatus;
import jakarta.validation.constraints.NotNull;

@Schema(description = "링크 공유 상태 변경 요청")
public record LinkShareStatusUpdateRequest(
        @Schema(description = "링크 공유 상태", example = "ACTIVE")
        @NotNull LinkShareStatus linkShareStatus
) {}
