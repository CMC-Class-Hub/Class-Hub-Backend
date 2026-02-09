package com.cmc.classhub.onedayClass.controller;

import com.cmc.classhub.onedayClass.dto.LinkShareStatusUpdateRequest;
import com.cmc.classhub.onedayClass.dto.OnedayClassCreateRequest;
import com.cmc.classhub.onedayClass.dto.OnedayClassDetailResponse;
import com.cmc.classhub.onedayClass.dto.OnedayClassResponse;
import com.cmc.classhub.onedayClass.service.OnedayClassService;
import com.cmc.classhub.onedayClass.dto.SessionResponse;
import com.cmc.classhub.onedayClass.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "OnedayClass", description = "원데이클래스 관리 API")
@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class OnedayClassController {

    private final OnedayClassService onedayClassService;
    private final SessionService sessionService;

    @Operation(summary = "내 클래스 목록 조회", description = "로그인한 강사의 모든 클래스를 조회합니다")
    @GetMapping
    public ResponseEntity<List<OnedayClassResponse>> getMyClasses(
            @AuthenticationPrincipal Long instructorId) {
        return ResponseEntity.ok(onedayClassService.getClassesByInstructor(instructorId));
    }

    @Operation(summary = "클래스 상세 조회", description = "특정 클래스의 상세 정보를 조회합니다")
    @GetMapping("/{classId}")
    public ResponseEntity<OnedayClassResponse> getClass(
            @Parameter(description = "클래스 ID") @PathVariable Long classId) {
        return ResponseEntity.ok(onedayClassService.getClassById(classId));
    }

    @Operation(summary = "클래스 생성", description = "새로운 원데이클래스를 생성합니다")
    @PostMapping
    public ResponseEntity<OnedayClassResponse> createClass(
            @AuthenticationPrincipal Long instructorId,
            @RequestBody @Valid OnedayClassCreateRequest request) {
        Long classId = onedayClassService.createOnedayClass(request, instructorId);
        return ResponseEntity.ok(onedayClassService.getClassById(classId));
    }

    @Operation(summary = "클래스 수정", description = "클래스 정보를 수정합니다")
    @PutMapping("/{classId}")
    public ResponseEntity<OnedayClassResponse> updateClass(
            @Parameter(description = "클래스 ID") @PathVariable Long classId,
            @RequestBody @Valid OnedayClassCreateRequest request) {
        onedayClassService.updateOnedayClass(classId, request);
        return ResponseEntity.ok(onedayClassService.getClassById(classId));
    }

    @Operation(summary = "클래스 삭제", description = "클래스를 삭제합니다")
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClass(
            @Parameter(description = "클래스 ID") @PathVariable Long classId) {
        onedayClassService.deleteClass(classId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "클래스의 세션 목록 조회", description = "특정 클래스의 모든 세션을 조회합니다")
    @GetMapping("/{classId}/sessions")
    public ResponseEntity<List<SessionResponse>> getClassSessions(
            @Parameter(description = "클래스 ID") @PathVariable Long classId) {
        return ResponseEntity.ok(sessionService.getSessionsByClassId(classId));
    }

    @Operation(summary = "링크 공유 상태 변경", description = "클래스의 링크 공유 활성화/비활성화 상태를 변경합니다")
    @PatchMapping("/{classId}/link-share-status")
    public ResponseEntity<OnedayClassResponse> updateLinkShareStatus(
            @Parameter(description = "클래스 ID") @PathVariable Long classId,
            @RequestBody @Valid LinkShareStatusUpdateRequest request) {
        onedayClassService.updateLinkShareStatus(classId, request);
        return ResponseEntity.ok(onedayClassService.getClassById(classId));
    }
}