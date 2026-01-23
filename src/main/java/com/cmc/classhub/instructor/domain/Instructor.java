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

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private Boolean isBusiness;

    private Long activeSettlementAccountId; // 현재 활성 정산 계좌 ID

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Instructor(String loginId, String password, String name, String phone, Boolean isBusiness) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.isBusiness = isBusiness;
        this.createdAt = LocalDateTime.now();
    }


    // 활성 계좌 변경 (활성 계좌는 1개만)
    public void changeActiveSettlementAccount(SettlementAccount newAccount, SettlementAccount currentActiveAccount) {
        // 1) 기존 활성 계좌가 있다면 비활성화
        if (currentActiveAccount != null) {
            currentActiveAccount.deactivate();
        }

        // 2) 새 계좌 활성화
        newAccount.activate();

        // 3) Instructor의 활성 계좌 ID 갱신
        this.activeSettlementAccountId = newAccount.getId();
    }
}
