package com.cmc.classhub.OnedayClass.domain;

public enum SessionStatus {
    RECRUITING, // 예약 가능 (잔여석 있음)
    FULL,       // 정원 초과 (예약 불가, 취소 발생 시 RECRUITING 전환 가능)
    FINISHED;   // 수업 종료 (날짜/시간이 지나서 완전히 완료됨)

    public boolean isJoinable() {
        return this == RECRUITING;
    }

    // 인원 추가 시 상태 변경 검증
    public void validateJoinable() {
        if (this == FULL) {
            throw new IllegalStateException("정원이 꽉 찬 세션입니다.");
        }
        if (this == FINISHED) {
            throw new IllegalStateException("이미 종료된 수업입니다.");
        }
    }
}
