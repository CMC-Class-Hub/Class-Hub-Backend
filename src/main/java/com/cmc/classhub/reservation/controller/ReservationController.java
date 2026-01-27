package com.cmc.classhub.reservation.controller;

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

    @PostMapping
    public ResponseEntity<Long> reserve(
            @RequestParam Long onedayClassId,
            @RequestBody @Valid ReservationRequest request
            // 실제 환경에서는 인증 객체(Member)를 시큐리티에서 받아와야 합니다.
    ) {
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
            @RequestParam String phone
    ) {
        List<ReservationDetailResponse> results = reservationService.searchMyReservations(name, phone);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }


}