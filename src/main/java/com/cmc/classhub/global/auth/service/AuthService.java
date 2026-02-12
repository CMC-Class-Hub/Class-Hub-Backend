package com.cmc.classhub.global.auth.service;

import com.cmc.classhub.global.auth.dto.LoginRequest;
import com.cmc.classhub.global.auth.dto.LoginResponse;
import com.cmc.classhub.global.auth.dto.LoginResultDto;
import com.cmc.classhub.global.auth.dto.TokenDto;
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
        if (instructorRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String hash = passwordEncoder.encode(req.password());
        Instructor instructor = new Instructor(req.name(), req.email(), req.phoneNumber(), hash);
        instructorRepository.save(instructor);
        return instructor.getId();
    }

    @Transactional(readOnly = true)
    public LoginResultDto login(LoginRequest req) {
        Instructor instructor = instructorRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.password(), instructor.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(instructor.getId(), instructor.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(instructor.getId());

        LoginResponse loginResponse = new LoginResponse(instructor.getId(), instructor.getName(),
                instructor.getPhoneNumber());
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);

        return new LoginResultDto(loginResponse, tokenDto);
    }

    public TokenDto refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        Long userId = jwtProvider.parseUserId(refreshToken);
        Instructor instructor = instructorRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtProvider.createAccessToken(instructor.getId(), instructor.getEmail());
        String newRefreshToken = jwtProvider.createRefreshToken(instructor.getId());

        return new TokenDto(newAccessToken, newRefreshToken);
    }
}
