package com.cmc.classhub.reservation.controller;

import com.cmc.classhub.reservation.dto.CreateMemberRequest;
import com.cmc.classhub.reservation.dto.MemberResponseDto;
import com.cmc.classhub.reservation.dto.UpdateMemberRequest;
import com.cmc.classhub.reservation.service.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class MemberController {

    private final MemberService MemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> getAll() {
        return ResponseEntity.ok(MemberService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(MemberService.getById(id));
    }

    @PostMapping
    public ResponseEntity<MemberResponseDto> create(
            @RequestBody CreateMemberRequest request) {
        return ResponseEntity.ok(MemberService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MemberResponseDto> update(
            @PathVariable Long id,
            @RequestBody UpdateMemberRequest request) {
        return ResponseEntity.ok(MemberService.update(id, request));
    }
}
