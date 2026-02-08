package com.cmc.classhub.global.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Presigned URL 응답")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {
    @Schema(description = "S3 업로드용 Presigned URL", example = "https://s3.amazonaws.com/bucket/...")
    private String uploadUrl;

    @Schema(description = "실제 파일 접근 URL", example = "https://s3.amazonaws.com/bucket/file.jpg")
    private String fileUrl;

    @Schema(description = "저장된 파일명", example = "uuid-file.jpg")
    private String fileName;
}