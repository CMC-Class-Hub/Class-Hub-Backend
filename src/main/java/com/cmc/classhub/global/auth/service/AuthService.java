package com.cmc.classhub.global.auth.service;

import com.cmc.classhub.global.auth.dto.LoginRequest;
import com.cmc.classhub.global.auth.dto.LoginResponse;
import com.cmc.classhub.global.auth.dto.SignUpRequest;
import com.cmc.classhub.global.auth.jwt.JwtProvider;
import com.cmc.classhub.instructor.domain.Instructor;
import com.cmc.classhub.instructor.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public Long signUp(SignUpRequest req) {
        if (!req.password().equals(req.passwordConfirm())) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }
        if (instructorRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String hash = passwordEncoder.encode(req.password());
        Instructor instructor = new Instructor(null,req.name(), req.email(), req.phoneNumber(), hash);
        instructorRepository.save(instructor);
        return instructor.getId();
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        Instructor instructor = instructorRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.password(), instructor.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtProvider.createAccessToken(instructor.getId(), instructor.getEmail());
        return new LoginResponse(instructor.getId(), token);
    }
}