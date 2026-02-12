package com.cmc.classhub.instructor.controller;

import com.cmc.classhub.onedayClass.dto.OnedayClassDetailResponse;
import com.cmc.classhub.instructor.service.InstructorService;
import com.cmc.classhub.instructor.dto.InstructorUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Instructor", description = "강사 관리 API")
@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @Operation(summary = "강사의 클래스 목록 조회", description = "특정 강사의 모든 클래스를 상세 정보와 함께 조회합니다")
    @GetMapping("/{instructorId}/classes")
    public ResponseEntity<List<OnedayClassDetailResponse>> getMyClasses(
            @Parameter(description = "강사 ID") @PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorService.getMyClasses(instructorId));
    }

    @Operation(summary = "강사 정보 수정", description = "강사 정보를 수정합니다")
    @PutMapping("/{instructorId}")
    public ResponseEntity<Void> updateInstructor(
            @Parameter(description = "강사 ID") @PathVariable Long instructorId,
            @RequestBody InstructorUpdateRequest request) {
        instructorService.updateInstructor(instructorId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강사 탈퇴", description = "강사 정보를 삭제(탈퇴) 처리하고 관련 클래스들도 모두 삭제합니다")
    @DeleteMapping("/{instructorId}")
    public ResponseEntity<Void> withdraw(
            @Parameter(description = "강사 ID") @PathVariable Long instructorId) {
        instructorService.withdraw(instructorId);
        return ResponseEntity.ok().build();
    }
}