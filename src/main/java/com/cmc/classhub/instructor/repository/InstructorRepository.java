package com.cmc.classhub.instructor.repository;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.instructor.domain.Instructor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByBusinessNameAndNameAndPhoneNumber(String bName, String name, String pNumber);
}
