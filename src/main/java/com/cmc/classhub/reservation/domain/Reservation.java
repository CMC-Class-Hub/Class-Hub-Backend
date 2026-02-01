package com.cmc.classhub.reservation.domain;

import com.cmc.classhub.member.domain.Member;
import com.cmc.classhub.onedayClass.domain.Session;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status; 
    // PENDING, CONFIRMED, CANCELLED, TIMEOUT, ATTENDED, NO_SHOW

    private LocalDateTime confirmedAt;   // 확정 일시
    private LocalDateTime cancelledAt;   // 취소 일시

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Reservation(Session session, Member member) {
        this.session = session;
        this.member = member;
        this.status = ReservationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // 신청 생성 (정적 팩토리 메서드)
    public static Reservation apply(Session session, Member member) {
        return Reservation.builder()
                .session(session)
                .member(member)
                .build();
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
}