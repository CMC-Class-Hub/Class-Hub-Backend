package com.cmc.classhub.global.init;

import com.cmc.classhub.instructor.domain.Instructor;
import com.cmc.classhub.instructor.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final InstructorRepository instructorRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    String adminEmail = "admin@admin.admin";

    if (instructorRepository.existsByEmail(adminEmail)) {
      return;
    }

    Instructor admin = Instructor.builder()
        .name("admin")
        .email(adminEmail)
        .phoneNumber("010-0000-000")
        .passwordHash(passwordEncoder.encode("djemals1234"))
        .build();

    instructorRepository.save(admin);
  }
}
