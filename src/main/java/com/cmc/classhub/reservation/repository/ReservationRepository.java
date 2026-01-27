package com.cmc.classhub.reservation.repository;

import com.cmc.classhub.reservation.domain.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllBySessionId(Long sessionId);
    boolean existsBySessionIdAndMemberId(Long sessionId, Long memberId);
}