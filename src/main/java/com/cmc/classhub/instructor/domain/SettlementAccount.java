package com.cmc.classhub.instructor.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "settlement_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long instructorId;

    @Column(nullable = false)
    private String bankName; // 은행

    @Column(nullable = false)
    private String accountNumber; // 계좌

    @Column(nullable = false)
    private String accountHolder; // 예금주

    @Column(nullable = false)
    private Boolean isActive; // 활성계좌 여부

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public SettlementAccount(Long instructorId, String bankName, String accountNumber, String accountHolder) {
        this.instructorId = instructorId;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.isActive = false;
        this.createdAt = LocalDateTime.now();
    }

    // 활성화
    public void activate() {
        this.isActive = true;
    }

    // 비활성화
    public void deactivate() {
        this.isActive = false;
    }
}
