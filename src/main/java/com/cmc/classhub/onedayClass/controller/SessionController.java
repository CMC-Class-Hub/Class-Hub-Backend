package com.cmc.classhub.onedayClass.controller;

import com.cmc.classhub.onedayClass.dto.SessionCreateRequest;
import com.cmc.classhub.onedayClass.dto.SessionResponse;
import com.cmc.classhub.onedayClass.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.cmc.classhub.reservation.service.ReservationService;
import com.cmc.classhub.reservation.dto.ReservationResponse;
import com.cmc.classhub.onedayClass.dto.SessionUpdateRequest;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final ReservationService reservationService;

    // 1. 세션 생성
    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
            @RequestBody @Valid SessionCreateRequest request) {
        Long sessionId = sessionService.createSession(request);
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    // 2. 세션 조회
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    // 3. 세션 수정
    @PutMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable Long sessionId,
            @RequestBody @Valid SessionUpdateRequest request) {
        sessionService.updateSession(sessionId, request);
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    // 4. 세션 상태 변경
    @PatchMapping("/{sessionId}/status")
    public ResponseEntity<SessionResponse> updateSessionStatus(
            @PathVariable Long sessionId,
            @RequestParam String status) {
        sessionService.updateSessionStatus(sessionId, status);
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    // 5. 세션 삭제
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    // 6. 세션의 신청 목록 조회
    @GetMapping("/{sessionId}/applications")
    public ResponseEntity<List<ReservationResponse>> getSessionApplications(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(reservationService.getReservationsBySession(sessionId));
    }

}