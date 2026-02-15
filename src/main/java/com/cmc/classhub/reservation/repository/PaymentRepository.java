package com.cmc.classhub.reservation.repository;

import com.cmc.classhub.reservation.domain.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByTid(String tid);

  Optional<Payment> findByOrderId(String orderId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Payment p where p.orderId = :orderId")
  Optional<Payment> findByOrderIdWithLock(String orderId);

  Optional<Payment> findByReservationId(Long reservationId);
}
