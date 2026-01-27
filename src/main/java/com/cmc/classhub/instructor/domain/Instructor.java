package com.cmc.classhub.instructor.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "instructors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String businessName; // 상호명

    @Column(nullable = false)
    private String name; // 이름

    @Column(nullable = false)
    private String email; // 이메일

    @Column(nullable = false)
    private String phoneNumber; // 전화번호

    @Column(nullable=false, length=100)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Instructor(String businessName, String name, String email, String phoneNumber, String passwordHash) {
        this.businessName = businessName;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
    }

}
