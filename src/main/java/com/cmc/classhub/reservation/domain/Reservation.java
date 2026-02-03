package com.cmc.classhub.reservation.domain;

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
        this.status = ReservationStatus.CONFIRMED;
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
        if (this.status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

}