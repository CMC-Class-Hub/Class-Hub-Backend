package com.cmc.classhub.global.init;

import com.cmc.classhub.global.auth.domain.Role;
import com.cmc.classhub.instructor.domain.Instructor;
import com.cmc.classhub.instructor.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
//@Profile({"local", "dev"})
@RequiredArgsConstructor
public class TestInitializer implements ApplicationRunner {

    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String demoEmail = "test@classhub.com";

        if (instructorRepository.existsByEmail(demoEmail)) {
            return; // 멱등성 보장
        }

        Instructor demoInstructor = Instructor.builder()
            .name("test")
            .email(demoEmail)
            .phoneNumber("010-0000-0000")
            .passwordHash(passwordEncoder.encode("demo1234"))
            .role(Role.USER)
            .build();

        instructorRepository.save(demoInstructor);
    }
}
