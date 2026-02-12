package com.cmc.classhub.admin.service;

import com.cmc.classhub.admin.dto.InstructorAdminResponse;
import com.cmc.classhub.instructor.repository.InstructorRepository;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

  private final InstructorRepository instructorRepository;
  private final OnedayClassRepository onedayClassRepository;

  public List<InstructorAdminResponse> getAllInstructorsInfo() {
    return instructorRepository.findByIsDeletedFalse().stream()
        .map(instructor -> {
          long classCount = onedayClassRepository.countByInstructorId(instructor.getId());
          long sessionCount = onedayClassRepository.countSessionsByInstructorId(instructor.getId());
          long reservationCount = onedayClassRepository.sumReservationsByInstructorId(instructor.getId());

          return new InstructorAdminResponse(
              instructor.getName(),
              instructor.getEmail(),
              instructor.getCreatedAt(),
              classCount,
              sessionCount,
              reservationCount);
        })
        .toList();
  }
}
