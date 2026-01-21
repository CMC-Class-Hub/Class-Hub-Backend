package com.cmc.classhub.reservation.service;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.port.OnedayClassReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationWriter {

    private final OnedayClassReader onedayClassReader;

    public Long createReservation(ReservationRequest request, Long memberId) {
        OnedayClass onedayClass = onedayClassRepository.findByIdWithLock(request.getOnedayClassId())
                .orElseThrow(() -> new IllegalArgumentException("클래스를 찾을 수 없습니다."));


        onedayClass.occupantSession(request.getSessionId());

        // 3. 예약 객체 생성 및 저장 (생성은 예약 도메인의 책임)
        Reservation reservation = Reservation.builder()
                .sessionId(request.getSessionId())
                .memberId(memberId)
                .applicantName(request.getApplicantName())
                .phoneNumber(request.getPhoneNumber())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        return reservationRepository.save(reservation).getId();
    }
}