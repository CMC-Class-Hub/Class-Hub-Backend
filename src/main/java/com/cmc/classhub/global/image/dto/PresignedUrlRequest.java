package com.cmc.classhub.global.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Presigned URL 요청")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequest {
    @Schema(description = "원본 파일명", example = "my-image.jpg")
    private String fileName;

    @Schema(description = "MIME 타입", example = "image/jpeg")
    private String fileType;
}

