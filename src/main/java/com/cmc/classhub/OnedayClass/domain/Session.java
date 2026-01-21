package com.cmc.classhub.OnedayClass.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date; // 날짜

    @Column(nullable = false)
    private LocalTime startTime; // 시작시간

    @Column(nullable = false)
    private LocalTime endTime; // 종료시간

    @Embedded
    private EnrollmentCount enrollmentCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status; // 상태 (오픈 / 마감)

    private boolean isDeleted = false; // 삭제 플래그

    @Builder
    public Session(LocalDate date, LocalTime startTime, LocalTime endTime, Integer capacity) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.enrollmentCount = new EnrollmentCount(capacity);
        this.status = SessionStatus.RECRUITING;
    }

    // 세션 정보 수정 (null이 아닌 값만 수정)
    public void update(Session session) {
        if (session.getDate() != null) this.date = session.getDate();
        if (session.getStartTime() != null) this.startTime = session.getStartTime();
        if (session.getEndTime() != null) this.endTime = session.getEndTime();
        if (session.getEnrollmentCount() != null) {
            this.enrollmentCount = this.enrollmentCount.updateCapacity(
                    session.getEnrollmentCount().getCapacity()
            );
        }
    }

    public void join() {
        this.status.validateJoinable();

        this.enrollmentCount = this.enrollmentCount.increase();

        // 정원이 다 차면 상태를 FULL로 변경
        if (this.enrollmentCount.isFull()) {
            this.status = SessionStatus.FULL;
        }
    }

    public void cancel() {
        this.enrollmentCount = this.enrollmentCount.decrease();

        // FULL 상태였는데 취소가 발생하면 RECRUITING으로 변경
        if (this.status == SessionStatus.FULL && !this.enrollmentCount.isFull()) {
            this.status = SessionStatus.RECRUITING;
        }
    }

    public void finish() {
        this.status = SessionStatus.FINISHED;
    }

}
