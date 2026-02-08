package com.cmc.classhub.global.init;

import com.cmc.classhub.instructor.domain.Instructor;
import com.cmc.classhub.instructor.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
//@Profile({"local", "dev"})
@RequiredArgsConstructor
public class InstructorDemoInitializer implements ApplicationRunner {

    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String demoEmail = "demo@classhub.test";

        if (instructorRepository.existsByEmail(demoEmail)) {
            return; // 멱등성 보장
        }

        Instructor demoInstructor = Instructor.builder()
            .name("데모 강사")
            .email(demoEmail)
            .phoneNumber("010-0000-0000")
            .passwordHash(passwordEncoder.encode("demo1234"))
            .build();

        instructorRepository.save(demoInstructor);
    }
}
