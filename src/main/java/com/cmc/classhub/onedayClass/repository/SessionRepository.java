package com.cmc.classhub.onedayClass.repository;

import com.cmc.classhub.onedayClass.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByDate(LocalDate date);
}
