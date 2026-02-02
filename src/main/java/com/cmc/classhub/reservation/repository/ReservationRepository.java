package com.cmc.classhub.reservation.repository;

import com.cmc.classhub.reservation.domain.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cmc.classhub.reservation.domain.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllBySessionId(Long sessionId);

    boolean existsBySessionIdAndMemberId(Long sessionId, Long memberId);

    List<Reservation> findBySessionIdAndStatus(Long sessionId, ReservationStatus status);
}