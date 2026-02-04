package com.cmc.classhub.reservation.repository;

import com.cmc.classhub.reservation.domain.Member;
import com.cmc.classhub.reservation.domain.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import com.cmc.classhub.reservation.domain.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllBySessionId(Long sessionId);


    boolean existsBySessionIdAndMemberId(Long sessionId, Long memberId);

    List<Reservation> findBySessionIdAndStatus(Long sessionId, ReservationStatus status);

    // ID 기반 쿼리
    boolean existsBySessionIdAndMember(Long sessionId, Member member);

    // ID 기반 쿼리도 제공 (호환성)
    @Query("SELECT r FROM Reservation r WHERE r.sessionId = :sessionId")
    List<Reservation> findAllBySessionId(@Param("sessionId") Long sessionId);

    // 상태별 조회 추가
    @Query("SELECT r FROM Reservation r WHERE r.sessionId = :sessionId AND r.status IN ('PENDING', 'CONFIRMED')")
    List<Reservation> findActiveReservationsBySessionId(@Param("sessionId") Long sessionId);
}