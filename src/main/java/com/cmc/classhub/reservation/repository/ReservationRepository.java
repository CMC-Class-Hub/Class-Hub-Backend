package com.cmc.classhub.reservation.repository;

import com.cmc.classhub.reservation.domain.Member;
import com.cmc.classhub.reservation.domain.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import com.cmc.classhub.reservation.domain.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findBySessionIdAndStatus(Long sessionId, ReservationStatus status);

    // 예약 코드로 조회
    java.util.Optional<Reservation> findByReservationCode(String reservationCode);

    // ID 기반 쿼리
    boolean existsBySessionIdAndMemberAndStatus(Long sessionId, Member member, ReservationStatus status);

    List<Reservation> findBySessionIdAndMemberAndStatus(Long sessionId, Member member, ReservationStatus status);

    // ID 기반 쿼리도 제공 (호환성)
    @Query("SELECT r FROM Reservation r WHERE r.sessionId = :sessionId")
    List<Reservation> findAllBySessionId(@Param("sessionId") Long sessionId);

    // 상태별 조회 추가
    @Query("SELECT r FROM Reservation r WHERE r.sessionId = :sessionId AND r.status IN ('PENDING', 'CONFIRMED')")
    List<Reservation> findActiveReservationsBySessionId(@Param("sessionId") Long sessionId);

    @Query(value = """
                SELECT r.*
                FROM reservations r
                JOIN members m ON r.member_id = m.id
                WHERE r.session_id = :sessionId
                AND (m.name, m.phone, r.created_at) IN (
                    SELECT m2.name, m2.phone, MAX(r2.created_at)
                    FROM reservations r2
                    JOIN members m2 ON r2.member_id = m2.id
                    WHERE r2.session_id = :sessionId
                    GROUP BY m2.name, m2.phone
                )
                ORDER BY r.created_at DESC
            """, nativeQuery = true)
    List<Reservation> findLatestReservationsBySession(@Param("sessionId") Long sessionId);

    @Query("SELECT r FROM Reservation r WHERE r.sessionId IN :sessionIds")
    List<Reservation> findAllBySessionIdIn(@Param("sessionIds") List<Long> sessionIds);

    // 회원별 최신순 조회
    List<Reservation> findByMemberIdOrderByIdDesc(Long memberId);

    // 세션 목록별 최신순 조회
    List<Reservation> findBySessionIdInOrderByIdDesc(List<Long> sessionIds);

    // 만료된 대기 예약 조회 (15분 경과)
    List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatus status, java.time.LocalDateTime dateTime);
}