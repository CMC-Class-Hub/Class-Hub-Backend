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

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(nullable = false, unique = true)
    private String reservationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status; // CONFIRMED, CANCELLED

    private LocalDateTime confirmedAt; // 확정 일시
    private LocalDateTime cancelledAt; // 취소 일시

    @Column(nullable = false)
    private boolean sentD3Notification = false; // D-3 알림 발송 여부

    @Column(nullable = false)
    private boolean sentD1Notification = false; // D-1 알림 발송 여부

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Reservation(Long sessionId, Member member) {
        this.sessionId = sessionId;
        this.member = member;
        this.reservationCode = java.util.UUID.randomUUID().toString();
        this.status = ReservationStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    // 신청 생성 (정적 팩토리 메서드)
    public static Reservation apply(Long sessionId, Member member) {
        return Reservation.builder()
                .sessionId(sessionId)
                .member(member)
                .build();
    }

    // 취소
    public void cancel() {
        if (this.status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    // D-3 알림 발송 완료 처리
    public void markD3NotificationSent() {
        this.sentD3Notification = true;
    }

    // D-1 알림 발송 완료 처리
    public void markD1NotificationSent() {
        this.sentD1Notification = true;
    }

}