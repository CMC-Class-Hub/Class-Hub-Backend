package com.cmc.classhub.reservation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status; // PENDING, CONFIRMED, CANCELLED, TIMEOUT, ATTENDED, NO_SHOW

    private LocalDateTime expiresAt; // 미결제 타임아웃

    private LocalDateTime confirmedAt; // 확정 일시

    private LocalDateTime cancelledAt; // 취소 일시

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Reservation(Long memberId, Long sessionId, LocalDateTime expiresAt) {
        this.memberId = memberId;
        this.sessionId = sessionId;
        this.status = ReservationStatus.PENDING;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }

    // 신청 생성 (정적 팩토리 메서드)
    public static Reservation apply(Long memberId, Long sessionId, LocalDateTime expiresAt) {
        return Reservation.builder()
                .memberId(memberId)
                .sessionId(sessionId)
                .expiresAt(expiresAt)
                .build();
    }

    // 결제 성공 후 확정
    public void confirm() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 예약만 확정할 수 있습니다.");
        }
        this.status = ReservationStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    // 취소
    public void cancel() {
        if (this.status == ReservationStatus.CANCELLED || this.status == ReservationStatus.TIMEOUT) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        if (this.status == ReservationStatus.ATTENDED || this.status == ReservationStatus.NO_SHOW) {
            throw new IllegalStateException("완료된 예약은 취소할 수 없습니다.");
        }
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    // 출석 처리
    public void markAttended() {
        if (this.status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("확정된 예약만 출석 처리할 수 있습니다.");
        }
        this.status = ReservationStatus.ATTENDED;
    }

    // 노쇼 처리
    public void markNoShow() {
        if (this.status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("확정된 예약만 노쇼 처리할 수 있습니다.");
        }
        this.status = ReservationStatus.NO_SHOW;
    }

    // 미결제 타임아웃 자동취소
    public void timeoutExpire() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 예약만 타임아웃 처리할 수 있습니다.");
        }
        this.status = ReservationStatus.TIMEOUT;
        this.cancelledAt = LocalDateTime.now();
    }
}
