package com.cmc.classhub.reservation.controller;

import com.cmc.classhub.reservation.dto.CreateMemberRequest;
import com.cmc.classhub.reservation.dto.MemberResponseDto;
import com.cmc.classhub.reservation.dto.UpdateMemberRequest;
import com.cmc.classhub.reservation.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Tag(name = "Member", description = "회원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService MemberService;

    @Operation(summary = "전체 회원 조회", description = "모든 회원 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> getAll() {
        return ResponseEntity.ok(MemberService.getAll());
    }

    @Operation(summary = "회원 상세 조회", description = "특정 회원의 상세 정보를 조회합니다")
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getById(
            @Parameter(description = "회원 ID") @PathVariable Long id) {
        return ResponseEntity.ok(MemberService.getById(id));
    }

    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다")
    @PostMapping
    public ResponseEntity<MemberResponseDto> create(
            @RequestBody CreateMemberRequest request) {
        return ResponseEntity.ok(MemberService.create(request));
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다")
    @PatchMapping("/{id}")
    public ResponseEntity<MemberResponseDto> update(
            @Parameter(description = "회원 ID") @PathVariable Long id,
            @RequestBody UpdateMemberRequest request) {
        return ResponseEntity.ok(MemberService.update(id, request));
    }
}
