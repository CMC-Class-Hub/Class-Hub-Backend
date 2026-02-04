package com.cmc.classhub.onedayClass.repository;

import com.cmc.classhub.onedayClass.domain.OnedayClass;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OnedayClassRepository extends JpaRepository<OnedayClass, Long> {
    Optional<OnedayClass> findByClassCode(String classCode);

    @Query("select o from OnedayClass o left join fetch o.sessions where o.classCode = :classCode")
    Optional<OnedayClass> findByClassCodeWithSessions(@Param("classCode") String classCode);

    List<OnedayClass> findAllByInstructorId(Long instructorId);

    @Query("select o from OnedayClass o join o.sessions s where s.id = :sessionId")
    Optional<OnedayClass> findBySessionsId(Long sessionId);

    
    List<OnedayClass> findByInstructorIdAndIsDeletedFalse(Long instructorId);
   

    @Query("SELECT oc FROM OnedayClass oc JOIN oc.sessions s " +
       "WHERE s.id = :sessionId AND oc.isDeleted = false AND s.isDeleted = false")
    Optional<OnedayClass> findBySessionsIdAndNotDeleted(@Param("sessionId") Long sessionId);

}
