package com.cmc.classhub.global.image.infrastructure;

import com.cmc.classhub.global.image.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "image", name = "provider", havingValue = "s3")
public class S3ImageClient implements ImageClient {

  private final S3Presigner s3Presigner;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  @Value("${aws.s3.region}")
  private String region;

  @Override
  public PresignedUrlResponse generatePresignedUrl(String fileName, String fileType) {
    String fileExtension = getFileExtension(fileName);
    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
    String s3Key = "class-images/" + uniqueFileName;

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(s3Key)
        .contentType(fileType)
        .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(15))
        .putObjectRequest(putObjectRequest)
        .build();

    PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
    String uploadUrl = presignedRequest.url().toString();

    String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
        bucketName, region, s3Key);

    return new PresignedUrlResponse(uploadUrl, fileUrl, uniqueFileName);
  }

  private String getFileExtension(String fileName) {
    if (fileName == null || !fileName.contains(".")) {
      return "";
    }
    return fileName.substring(fileName.lastIndexOf("."));
  }
}
