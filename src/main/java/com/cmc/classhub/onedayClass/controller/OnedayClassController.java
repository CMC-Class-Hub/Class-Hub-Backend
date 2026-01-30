package com.cmc.classhub.onedayClass.controller;

import com.cmc.classhub.onedayClass.dto.OnedayClassCreateRequest;
import com.cmc.classhub.onedayClass.dto.OnedayClassDetailResponse;
import com.cmc.classhub.onedayClass.service.OnedayClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class OnedayClassController {

    private final OnedayClassService onedayClassService;

    @GetMapping("/shared/{classCode}")
    public ResponseEntity<OnedayClassDetailResponse> getSharedClass(@PathVariable String classCode) {
        return ResponseEntity.ok(onedayClassService.getSharedClassDetail(classCode));
    }

    @PostMapping("/instructor")
    public ResponseEntity<Long> createClass(
            @RequestBody @Valid OnedayClassCreateRequest request
    // @AuthenticationPrincipal 등을 통해 강사 정보를 가져와야 함
    ) {
        // 테스트를 위해 임시 강사 ID 1L 사용
        Long instructorId = 1L;
        Long classId = onedayClassService.createOnedayClass(request, instructorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(classId);
    }

    @PutMapping("/{classId}")
    public ResponseEntity<Void> updateClass(
            @PathVariable Long classId,
            @RequestBody @Valid OnedayClassCreateRequest request) {
        onedayClassService.updateOnedayClass(classId, request);
        return ResponseEntity.ok().build();
    }

}