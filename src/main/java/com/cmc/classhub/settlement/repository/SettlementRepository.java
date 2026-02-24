package com.cmc.classhub.settlement.repository;

import com.cmc.classhub.settlement.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

  List<Settlement> findAllByInstructorId(Long instructorId);

  @Query("SELECT s FROM Settlement s WHERE s.reservationId IN (SELECT r.id FROM Reservation r WHERE r.sessionId = :sessionId)")
  List<Settlement> findAllBySessionId(@Param("sessionId") Long sessionId);

  java.util.Optional<Settlement> findByReservationId(Long reservationId);
}
