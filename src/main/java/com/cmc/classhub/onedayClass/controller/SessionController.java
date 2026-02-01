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

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final ReservationService reservationService;
    // 1. 세션 생성
    @PostMapping
    public SessionResponse createSession(
            @RequestBody @Valid SessionCreateRequest request) {
        Long sessionId = sessionService.createSession(request);
        return sessionService.getSessionById(sessionId);
    }

    // 2. 세션 조회
    @GetMapping("/{sessionId}")
    public SessionResponse getSession(@PathVariable Long sessionId) {
        return sessionService.getSessionById(sessionId);
    }

    // 3. 세션 수정
    @PutMapping("/{sessionId}")
    public SessionResponse updateSession(
            @PathVariable Long sessionId,
            @RequestBody @Valid SessionUpdateRequest request) {
        sessionService.updateSession(sessionId, request);
        return sessionService.getSessionById(sessionId);
    }

    // 4. 세션 상태 변경
    @PatchMapping("/{sessionId}/status")
    public SessionResponse updateSessionStatus(
            @PathVariable Long sessionId,
            @RequestParam String status) {
        sessionService.updateSessionStatus(sessionId, status);
        return sessionService.getSessionById(sessionId);
    }

    // 5. 세션 삭제
    @DeleteMapping("/{sessionId}")
    public void deleteSession(@PathVariable Long sessionId) {
        sessionService.deleteSession(sessionId);
    }
 
    // 6. 세션의 신청 목록 조회
    @GetMapping("/{sessionId}/applications")
    public List<ReservationResponse> getSessionApplications(
            @PathVariable Long sessionId) {
        return reservationService.getReservationsBySession(sessionId);
    }

  }