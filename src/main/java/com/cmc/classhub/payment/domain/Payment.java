package com.cmc.classhub.payment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method; // CARD, TRANSFER

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // SUCCESS, FAILED, REFUNDED

    private String pgTransactionId; // PG 거래 ID

    private LocalDateTime paidAt; // 결제 완료 일시

    private LocalDateTime refundedAt; // 환불 일시

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Payment(Long reservationId, PaymentMethod method, Integer amount) {
        this.reservationId = reservationId;
        this.method = method;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
