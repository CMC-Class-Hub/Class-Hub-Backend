package com.cmc.classhub.global.image.controller;

import com.cmc.classhub.global.image.dto.PresignedUrlRequest;
import com.cmc.classhub.global.image.dto.PresignedUrlResponse;
import com.cmc.classhub.global.image.infrastructure.ImageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

        private final ImageClient imageClient;

        // 허용할 이미지 타입
        private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
                        "image/jpeg",
                        "image/jpg",
                        "image/png",
                        "image/gif",
                        "image/webp");

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

                try {
                        PresignedUrlResponse response = imageClient.generatePresignedUrl(request.getFileName(),
                                        request.getFileType());
                        log.info("✅ Presigned URL 생성 완료 (Client: {})", imageClient.getClass().getSimpleName());
                        return ResponseEntity.ok(response);
                } catch (Exception e) {
                        log.error("❌ Presigned URL 생성 실패", e);
                        return ResponseEntity.internalServerError().build();
                }
        }
}