package com.cmc.classhub.instructor.controller;

import com.cmc.classhub.OnedayClass.dto.OnedayClassDetailResponse;
import com.cmc.classhub.instructor.dto.InstructorLoginRequest;
import com.cmc.classhub.instructor.service.InstructorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody InstructorLoginRequest request) {
        Long instructorId = instructorService.loginOrRegister(request);
        return ResponseEntity.ok(instructorId);
    }

    @GetMapping("/{instructorId}/classes")
    public ResponseEntity<List<OnedayClassDetailResponse>> getMyClasses(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorService.getMyClasses(instructorId));
    }

}