package com.cmc.classhub.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Member(String loginId, String password, String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }

    public void updateReservationInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

}
