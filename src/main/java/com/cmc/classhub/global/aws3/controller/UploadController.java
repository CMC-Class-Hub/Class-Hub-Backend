package com.cmc.classhub.global.aws3.controller;

import com.cmc.classhub.global.aws3.dto.PresignedUrlRequest;
import com.cmc.classhub.global.aws3.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // ⚠️ 프로덕션에서는 특정 도메인만 허용
public class UploadController {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    // 허용할 이미지 타입
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    // 최대 파일 크기 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(
            @RequestBody PresignedUrlRequest request) {
        log.info("📤 Presigned URL 요청: fileName={}, fileType={}", 
                request.getFileName(), request.getFileType());

        // 1. 파일 타입 검증
        if (!ALLOWED_CONTENT_TYPES.contains(request.getFileType())) {
            log.error("❌ 허용되지 않는 파일 타입: {}", request.getFileType());
            return ResponseEntity.badRequest().build();
        }

        // 2. 고유한 파일명 생성
        String fileExtension = getFileExtension(request.getFileName());
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        String s3Key = "class-images/" + uniqueFileName;

        log.info("🔑 S3 Key 생성: {}", s3Key);

        try {
            // 3. PutObjectRequest 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(request.getFileType())
                    .build();

            // 4. Presigned URL 생성 (15분 유효)
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = 
                    s3Presigner.presignPutObject(presignRequest);

            String uploadUrl = presignedRequest.url().toString();
            
            // 5. 실제 파일 접근 URL 생성
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, s3Key);

            log.info("✅ Presigned URL 생성 완료");
            log.info("   Upload URL: {}", uploadUrl);
            log.info("   File URL: {}", fileUrl);

            PresignedUrlResponse response = new PresignedUrlResponse(
                    uploadUrl,
                    fileUrl,
                    uniqueFileName
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Presigned URL 생성 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 헬스 체크 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Upload service is running");
    }
}