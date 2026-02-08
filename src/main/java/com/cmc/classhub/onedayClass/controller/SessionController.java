package com.cmc.classhub.onedayClass.controller;

import com.cmc.classhub.onedayClass.dto.SessionCreateRequest;
import com.cmc.classhub.onedayClass.dto.SessionResponse;
import com.cmc.classhub.onedayClass.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.cmc.classhub.reservation.service.ReservationService;
import com.cmc.classhub.reservation.dto.ReservationResponse;
import com.cmc.classhub.onedayClass.dto.SessionUpdateRequest;
import org.springframework.http.ResponseEntity;

@Tag(name = "Session", description = "세션 관리 API")
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final ReservationService reservationService;

    @Operation(summary = "세션 생성", description = "새로운 세션을 생성합니다")
    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
            @RequestBody @Valid SessionCreateRequest request) {
        Long sessionId = sessionService.createSession(request);
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    @Operation(summary = "세션 조회", description = "특정 세션의 상세 정보를 조회합니다")
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    @Operation(summary = "세션 수정", description = "세션 정보를 수정합니다")
    @PutMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> updateSession(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId,
            @RequestBody @Valid SessionUpdateRequest request) {
        sessionService.updateSession(sessionId, request);
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    @Operation(summary = "세션 상태 변경", description = "세션의 상태를 변경합니다")
    @PatchMapping("/{sessionId}/status")
    public ResponseEntity<SessionResponse> updateSessionStatus(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId,
            @Parameter(description = "변경할 상태") @RequestParam String status) {
        sessionService.updateSessionStatus(sessionId, status);
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    @Operation(summary = "세션 삭제", description = "세션을 삭제합니다")
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "세션 신청 목록 조회", description = "특정 세션의 예약 신청 목록을 조회합니다")
    @GetMapping("/{sessionId}/applications")
    public ResponseEntity<List<ReservationResponse>> getSessionApplications(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId) {
        return ResponseEntity.ok(reservationService.getReservationsBySession(sessionId));
    }
}