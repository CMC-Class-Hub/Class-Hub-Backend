package com.cmc.classhub.OnedayClass.repository;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OnedayClassRepository extends JpaRepository<OnedayClass, Long> {
    Optional<OnedayClass> findByShareCode(String shareCode);

    @Query("select o from OnedayClass o left join fetch o.sessions where o.shareCode = :shareCode")
    Optional<OnedayClass> findByShareCodeWithSessions(@Param("shareCode") String shareCode);
}
