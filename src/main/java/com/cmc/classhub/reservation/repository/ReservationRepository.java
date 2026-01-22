package com.cmc.classhub.reservation.repository;

import com.cmc.classhub.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}