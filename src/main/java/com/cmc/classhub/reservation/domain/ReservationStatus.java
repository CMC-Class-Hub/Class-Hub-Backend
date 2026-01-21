package com.cmc.classhub.reservation.domain;

public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    TIMEOUT,
    ATTENDED,
    NO_SHOW;

    // 확정 가능 여부 검증
    public void validateConfirmable() {
        if (this != PENDING) {
            throw new IllegalStateException("대기 중인 예약만 확정할 수 있습니다.");
        }
    }

    // 취소 가능 여부 검증
    public void validateCancelable() {
        if (this == CANCELLED || this == TIMEOUT) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        if (this == ATTENDED || this == NO_SHOW) {
            throw new IllegalStateException("완료된 예약은 취소할 수 없습니다.");
        }
    }

    // 출석 처리 가능 여부 검증
    public void validateAttendable() {
        if (this != CONFIRMED) {
            throw new IllegalStateException("확정된 예약만 출석 처리할 수 있습니다.");
        }
    }

    // 노쇼 처리 가능 여부 검증
    public void validateNoShowable() {
        if (this != CONFIRMED) {
            throw new IllegalStateException("확정된 예약만 노쇼 처리할 수 있습니다.");
        }
    }

    // 타임아웃 가능 여부 검증
    public void validateTimeoutable() {
        if (this != PENDING) {
            throw new IllegalStateException("대기 상태의 예약만 타임아웃 처리할 수 있습니다.");
        }
    }
}
