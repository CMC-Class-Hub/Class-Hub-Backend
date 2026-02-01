package com.cmc.classhub.instructor.service;

import com.cmc.classhub.onedayClass.dto.OnedayClassDetailResponse;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.instructor.domain.Instructor;
import com.cmc.classhub.instructor.dto.InstructorLoginRequest;
import com.cmc.classhub.instructor.repository.InstructorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final OnedayClassRepository onedayClassRepository;

    @Transactional
    public Long loginOrRegister(InstructorLoginRequest request) {
        return instructorRepository.findByNameAndPhoneNumber(
                        request.name(), request.phoneNumber())
                .orElseGet(() -> instructorRepository.save(
                        Instructor.builder()
                                .name(request.name())
                                .phoneNumber(request.phoneNumber())
                                .build()
                )).getId();
    }

    public List<OnedayClassDetailResponse> getMyClasses(Long instructorId) {
        return onedayClassRepository.findAllByInstructorId(instructorId).stream()
                .map(OnedayClassDetailResponse::from)
                .toList();
    }
}