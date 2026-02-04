package com.cmc.classhub.global.image.infrastructure;

import com.cmc.classhub.global.image.dto.PresignedUrlResponse;

public interface ImageClient {
  PresignedUrlResponse generatePresignedUrl(String fileName, String fileType);
}
