package com.cmc.classhub.onedayClass.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.cmc.classhub.onedayClass.domain.SessionStatus.FULL;
import static com.cmc.classhub.onedayClass.domain.SessionStatus.RECRUITING;

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
    private SessionStatus status; // 상태 (오픈 / 마감/ 종료)

    @Column(nullable = false)
    private boolean isDeleted = false;// 삭제 여부

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    @Builder
    public Session(
            OnedayClass onedayClass,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Integer capacity) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.currentNum = 0;
        this.status = RECRUITING;
    }

    public void updateStatus(SessionStatus status) {
        this.status = status;
    }

    // 세션 정보 수정 (null이 아닌 값만 수정)
    public void update(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Integer capacity) {
        if (date != null)
            this.date = date;
        if (startTime != null)
            this.startTime = startTime;
        if (endTime != null)
            this.endTime = endTime;
        if (capacity != null)
            this.capacity = capacity;
    }

    public void join() {

        if (this.currentNum >= this.capacity) {
            throw new IllegalStateException("정원이 초과되어 예약할 수 없습니다.");
        }

        if (this.status != RECRUITING) {
            throw new IllegalStateException("RECRUITING에서만 참여가 가능합니다.");
        }

        this.currentNum++;

        // 정원이 다 차면 상태를 FULL로 변경
        if (this.currentNum.equals(this.capacity)) {
            this.status = FULL;
        }
    }

    public void cancel() {
        if (this.currentNum > 0) {
            this.currentNum--;
            if (this.status == FULL) {
                this.status = RECRUITING;
            }
        }
    }

    

}
