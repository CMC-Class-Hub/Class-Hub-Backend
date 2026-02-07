package com.cmc.classhub.instructor.controller;

import com.cmc.classhub.onedayClass.dto.OnedayClassDetailResponse;
import com.cmc.classhub.instructor.service.InstructorService;
import com.cmc.classhub.instructor.dto.InstructorUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @GetMapping("/{instructorId}/classes")
    public ResponseEntity<List<OnedayClassDetailResponse>> getMyClasses(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorService.getMyClasses(instructorId));
    }
    @PutMapping("/{instructorId}")
    public ResponseEntity<Void> updateInstructor(
            @PathVariable Long instructorId,
            @RequestBody InstructorUpdateRequest request) {
        instructorService.updateInstructor(instructorId, request);
        return ResponseEntity.ok().build();
    }
}