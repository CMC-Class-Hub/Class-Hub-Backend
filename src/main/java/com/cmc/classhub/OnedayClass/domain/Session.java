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

    @Column(nullable = false)
    private Integer capacity; // 정원

    @Column(nullable = false)
    private Integer currentNum; // 현재 참여 인원

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status; // 상태 (오픈 / 마감)

    @Builder
    public Session(LocalDate date, LocalTime startTime, LocalTime endTime, Integer capacity) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.currentNum = 0;
        this.status = SessionStatus.RECRUITING;
    }

    // 세션 정보 수정 (null이 아닌 값만 수정)
    public void update(Session session) {
        if (session.getDate() != null) this.date = session.getDate();
        if (session.getStartTime() != null) this.startTime = session.getStartTime();
        if (session.getEndTime() != null) this.endTime = session.getEndTime();
        if (session.getCapacity() != null) this.capacity = session.getCapacity();
    }
}
