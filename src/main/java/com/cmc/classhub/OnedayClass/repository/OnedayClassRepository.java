package com.cmc.classhub.OnedayClass.repository;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnedayClassRepository extends JpaRepository<OnedayClass, Long> {
    Optional<OnedayClass> findByShareCode(String shareCode);
}