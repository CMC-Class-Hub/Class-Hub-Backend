package com.cmc.classhub.global.image.infrastructure;

import com.cmc.classhub.global.image.dto.PresignedUrlResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "image", name = "provider", havingValue = "console", matchIfMissing = true)
public class ConsoleImageClient implements ImageClient {

  @Override
  public PresignedUrlResponse generatePresignedUrl(String fileName, String fileType) {
    String fileExtension = getFileExtension(fileName);
    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

    // 가짜 URL 생성
    String uploadUrl = "http://localhost:8080/fake-upload/" + uniqueFileName;
    String fileUrl = "https://fake-s3-bucket.s3.ap-northeast-2.amazonaws.com/class-images/" + uniqueFileName;

    log.info("[IMAGE:CONSOLE] Presigned URL generated: fileName={}, uploadUrl={}, fileUrl={}",
        fileName, uploadUrl, fileUrl);

    return new PresignedUrlResponse(uploadUrl, fileUrl, uniqueFileName);
  }

  private String getFileExtension(String fileName) {
    if (fileName == null || !fileName.contains(".")) {
      return "";
    }
    return fileName.substring(fileName.lastIndexOf("."));
  }
}
