package com.cmc.classhub.instructor.service;

import com.cmc.classhub.onedayClass.domain.OnedayClass;
import com.cmc.classhub.onedayClass.dto.OnedayClassDetailResponse;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.instructor.domain.Instructor;
import com.cmc.classhub.instructor.dto.InstructorLoginRequest;
import com.cmc.classhub.instructor.dto.InstructorUpdateRequest;
import com.cmc.classhub.instructor.repository.InstructorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final OnedayClassRepository onedayClassRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long loginOrRegister(InstructorLoginRequest request) {
        return instructorRepository.findByNameAndPhoneNumber(
                request.name(), request.phoneNumber())
                .orElseGet(() -> instructorRepository.save(
                        Instructor.builder()
                                .name(request.name())
                                .phoneNumber(request.phoneNumber())
                                .build()))
                .getId();
    }

    public List<OnedayClassDetailResponse> getMyClasses(Long instructorId) {
        return onedayClassRepository.findAllByInstructorId(instructorId).stream()
                .map(OnedayClassDetailResponse::from)
                .toList();
    }

    @Transactional
    public void updateInstructor(Long instructorId, InstructorUpdateRequest request) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사를 찾을 수 없습니다."));

        instructor.updateInfo(request.name(), request.email(), request.phoneNumber());

        if (request.password() != null && !request.password().isBlank()) {
            instructor.updatePassword(passwordEncoder.encode(request.password()));
        }
    }

    @Transactional
    public void withdraw(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사를 찾을 수 없습니다."));

        instructor.delete();

        // 강사가 개설한 모든 클래스 삭제 처리
        onedayClassRepository.findByInstructorIdAndIsDeletedFalse(instructorId)
                .forEach(OnedayClass::delete);
    }
}