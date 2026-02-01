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
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class OnedayClassController {

    private final OnedayClassService onedayClassService;
    private final SessionService sessionService;

    // 1. 강사의 모든 클래스 조회
    @GetMapping
    public List<OnedayClassResponse> getMyClasses(
             @RequestParam Long instructorId) {
            System.out.println("Getclassses = " + instructorId);
            System.out.println("answer = " + onedayClassService.getClassesByInstructor(instructorId));
        return onedayClassService.getClassesByInstructor(instructorId);
    }

    // 2. 특정 클래스 조회
    @GetMapping("/{classId}")
    public OnedayClassResponse getClass(@PathVariable Long classId) {
        return onedayClassService.getClassById(classId);
    }

    // 3. 클래스 생성 (세션 없이)
    @PostMapping
    public OnedayClassResponse createClass(
            @RequestParam Long instructorId,
            @RequestBody @Valid OnedayClassCreateRequest request) {
        Long classId = onedayClassService.createOnedayClass(request, instructorId);
        return onedayClassService.getClassById(classId);
    }

    // 4. 클래스 수정
    @PutMapping("/{classId}")
    public OnedayClassResponse updateClass(
            @PathVariable Long classId,
            @RequestBody @Valid OnedayClassCreateRequest request) {
        onedayClassService.updateOnedayClass(classId, request);
        return onedayClassService.getClassById(classId);
    }

    // 5. 클래스 삭제
    @DeleteMapping("/{classId}")
    public void deleteClass(@PathVariable Long classId) {
        onedayClassService.deleteClass(classId);
    }
     
    // 6. 클래스의 세션 목록 조회
    @GetMapping("/{classId}/sessions")
    public List<SessionResponse> getClassSessions(@PathVariable Long classId) {
        System.out.println("세션 겟 = " + sessionService.getSessionsByClassId(classId));
        return sessionService.getSessionsByClassId(classId);
    }
    
     // 7. 클래스 코드로 클래스 조회
    @GetMapping("/code/{classCode}")
    public OnedayClassResponse getClassByCode(@PathVariable String classCode) {
        return onedayClassService.getClassByCode(classCode);
    }   
}