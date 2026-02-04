package com.cmc.classhub.reservation.controller;

import com.cmc.classhub.onedayClass.dto.OnedayClassResponse;
import com.cmc.classhub.onedayClass.dto.SessionResponse;
import com.cmc.classhub.onedayClass.service.OnedayClassService;
import com.cmc.classhub.onedayClass.service.SessionService;
import com.cmc.classhub.reservation.dto.ReservationDetailResponse;
import com.cmc.classhub.reservation.dto.ReservationRequest;
import com.cmc.classhub.reservation.dto.ReservationResponse;
import com.cmc.classhub.reservation.service.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final OnedayClassService onedayClassService;
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<Long> reserve(
            @RequestParam Long onedayClassId,
            @RequestBody @Valid ReservationRequest request) {
        Long reservationId = reservationService.createReservation(request, onedayClassId);
        return ResponseEntity.ok(reservationId);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<ReservationResponse>> getReservations(@PathVariable Long sessionId) {
        List<ReservationResponse> responses = reservationService.getReservationsBySession(sessionId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailResponse> getReservationDetail(@PathVariable Long reservationId) {
        ReservationDetailResponse response = reservationService.getReservationDetails(reservationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReservationDetailResponse>> searchReservations(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String password) {
        List<ReservationDetailResponse> results = reservationService.searchMyReservations(name, phone, password);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    // 클래스 코드로 클래스 조회 (공개용)
    @GetMapping("/code/{classCode}")
    public ResponseEntity<OnedayClassResponse> getClassByCode(@PathVariable String classCode) {
        return ResponseEntity.ok(onedayClassService.getClassByCode(classCode));
    }

    // 클래스 아이디로 세션 목록 조회 (공개용)
    @GetMapping("/{classId}/sessions")
    public ResponseEntity<List<SessionResponse>> getClassSessions(@PathVariable Long classId) {
        return ResponseEntity.ok(sessionService.getSessionsByClassId(classId));
    }
}
