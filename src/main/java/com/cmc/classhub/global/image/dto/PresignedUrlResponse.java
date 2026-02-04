package com.cmc.classhub.global.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {
    private String uploadUrl;    // S3 업로드용 Presigned URL
    private String fileUrl;      // 실제 파일 접근 URL
    private String fileName;     // 저장된 파일명
}