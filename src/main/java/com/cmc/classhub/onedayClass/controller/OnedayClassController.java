package com.cmc.classhub.onedayClass.controller;

import com.cmc.classhub.onedayClass.dto.OnedayClassCreateRequest;
import com.cmc.classhub.onedayClass.dto.OnedayClassDetailResponse;
import com.cmc.classhub.onedayClass.dto.OnedayClassResponse;
import com.cmc.classhub.onedayClass.service.OnedayClassService;
import com.cmc.classhub.onedayClass.dto.SessionResponse;
import com.cmc.classhub.onedayClass.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class OnedayClassController {

    private final OnedayClassService onedayClassService;
    private final SessionService sessionService;

    // 1. 강사의 모든 클래스 조회
    @GetMapping
    public ResponseEntity<List<OnedayClassResponse>> getMyClasses(
            @AuthenticationPrincipal Long instructorId) {
        System.out.println("Getclassses = " + instructorId);
        System.out.println("answer = " + onedayClassService.getClassesByInstructor(instructorId));
        return ResponseEntity.ok(onedayClassService.getClassesByInstructor(instructorId));
    }

    // 2. 특정 클래스 조회
    @GetMapping("/{classId}")
    public ResponseEntity<OnedayClassResponse> getClass(@PathVariable Long classId) {
        return ResponseEntity.ok(onedayClassService.getClassById(classId));
    }

    // 3. 클래스 생성 (세션 없이)
    @PostMapping
    public ResponseEntity<OnedayClassResponse> createClass(
            @AuthenticationPrincipal Long instructorId,
            @RequestBody @Valid OnedayClassCreateRequest request) {
        Long classId = onedayClassService.createOnedayClass(request, instructorId);
        return ResponseEntity.ok(onedayClassService.getClassById(classId));
    }

    // 4. 클래스 수정
    @PutMapping("/{classId}")
    public ResponseEntity<OnedayClassResponse> updateClass(
            @PathVariable Long classId,
            @RequestBody @Valid OnedayClassCreateRequest request) {
        onedayClassService.updateOnedayClass(classId, request);
        return ResponseEntity.ok(onedayClassService.getClassById(classId));
    }

    // 5. 클래스 삭제
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long classId) {
        onedayClassService.deleteClass(classId);
        return ResponseEntity.noContent().build();
    }

    // 6. 클래스의 세션 목록 조회
    @GetMapping("/{classId}/sessions")
    public ResponseEntity<List<SessionResponse>> getClassSessions(@PathVariable Long classId) {
        System.out.println("세션 겟 = " + sessionService.getSessionsByClassId(classId));
        return ResponseEntity.ok(sessionService.getSessionsByClassId(classId));
    }

    // 7. 클래스 코드로 클래스 조회
    @GetMapping("/code/{classCode}")
    public ResponseEntity<OnedayClassResponse> getClassByCode(@PathVariable String classCode) {
        return ResponseEntity.ok(onedayClassService.getClassByCode(classCode));
    }
}