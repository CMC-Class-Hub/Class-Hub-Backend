package com.cmc.classhub.global.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequest {
    private String fileName;    // 원본 파일명
    private String fileType;    // MIME 타입 (image/jpeg 등)
}

