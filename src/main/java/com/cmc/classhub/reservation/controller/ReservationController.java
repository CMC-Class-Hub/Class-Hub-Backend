package com.cmc.classhub.reservation.controller;

import com.cmc.classhub.reservation.dto.ReservationRequest;
import com.cmc.classhub.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Long> reserve(
            @RequestParam Long onedayClassId,
            @RequestBody @Valid ReservationRequest request
            // 실제 환경에서는 인증 객체(Member)를 시큐리티에서 받아와야 합니다.
    ) {
        // 테스트를 위해 임시로 memberId 1L을 사용합니다.
        Long memberId = 1L;
        Long reservationId = reservationService.createReservation(request, onedayClassId, memberId);
        return ResponseEntity.ok(reservationId);
    }
}