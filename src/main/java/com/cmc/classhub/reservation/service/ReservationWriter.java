package com.cmc.classhub.reservation.service;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.dto.ReservationRequest;
import com.cmc.classhub.reservation.port.OnedayClassReader;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationWriter {

    private final OnedayClassReader onedayClassReader;
    private final ReservationRepository reservationRepository;

    public Long createReservation(ReservationRequest request, Long onedayClassId, Long memberId) {
        OnedayClass onedayClass = onedayClassReader.getOnedayClass(onedayClassId)
                .orElseThrow(() -> new IllegalArgumentException("해당 세션이 포함된 클래스를 찾을 수 없습니다."));


        onedayClass.reserveSession(request.getSessionId());

        // 3. 예약 객체 생성 및 저장 (생성은 예약 도메인의 책임)
        Reservation reservation = Reservation.builder()
                .sessionId(request.getSessionId())
                .memberId(memberId)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        return reservationRepository.save(reservation).getId();
    }
}