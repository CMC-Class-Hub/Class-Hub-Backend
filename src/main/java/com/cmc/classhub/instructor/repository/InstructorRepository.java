package com.cmc.classhub.instructor.repository;

import com.cmc.classhub.instructor.domain.Instructor;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByNameAndPhoneNumber(String name, String pNumber);

    boolean existsByEmail(String email);

    Optional<Instructor> findByEmail(String email);

    java.util.List<Instructor> findByIsDeletedFalse();
}
