package com.cmc.classhub.reservation.repository;

import com.cmc.classhub.reservation.domain.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import com.cmc.classhub.onedayClass.domain.Session;
import com.cmc.classhub.member.domain.Member;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    boolean existsBySessionIdAndMemberId(Long sessionId, Long memberId);


    // 엔티티 기반 쿼리 (추천)
    List<Reservation> findAllBySession(Session session);
    boolean existsBySessionAndMember(Session session, Member member);

    // ID 기반 쿼리도 제공 (호환성)
    @Query("SELECT r FROM Reservation r WHERE r.session.id = :sessionId")
    List<Reservation> findAllBySessionId(@Param("sessionId") Long sessionId);

    // 상태별 조회 추가
    @Query("SELECT r FROM Reservation r WHERE r.session.id = :sessionId AND r.status IN ('PENDING', 'CONFIRMED')")
    List<Reservation> findActiveReservationsBySessionId(@Param("sessionId") Long sessionId);
}