package com.cmc.classhub.admin.service;

import com.cmc.classhub.admin.dto.InstructorAdminResponse;
import com.cmc.classhub.instructor.domain.Instructor;
import com.cmc.classhub.instructor.repository.InstructorRepository;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

  private final InstructorRepository instructorRepository;
  private final OnedayClassRepository onedayClassRepository;
  private final PasswordEncoder passwordEncoder;

  public List<InstructorAdminResponse> getAllInstructorsInfo() {
    return instructorRepository.findByIsDeletedFalse().stream()
        .map(instructor -> {
          long classCount = onedayClassRepository.countByInstructorId(instructor.getId());
          long sessionCount = onedayClassRepository.countSessionsByInstructorId(instructor.getId());
          long reservationCount = onedayClassRepository.sumReservationsByInstructorId(instructor.getId());

          return new InstructorAdminResponse(
              instructor.getId(),
              instructor.getName(),
              instructor.getEmail(),
              instructor.getCreatedAt(),
              classCount,
              sessionCount,
              reservationCount);
        })
        .toList();
  }

  @Transactional
  public void resetInstructorPassword(Long instructorId) {
    Instructor instructor = instructorRepository.findById(instructorId)
        .orElseThrow(() -> new IllegalArgumentException("강사를 찾을 수 없습니다."));

    // 비밀번호를 '0000'으로 초기화 (암호화하여 저장)
    instructor.updatePassword(passwordEncoder.encode("0000"));
  }
}
