package com.cmc.classhub.OnedayClass.domain;

public enum OnedayClassStatus {
    RECRUITING, // 이 클래스는 현재 살아있고 새로운 세션을 추가하거나 홍보할 수 있음
    CLOSED; // 강사가 일시적으로 이 클래스의 모든 예약을 막음

    // 모집 마감(Close) 가능 여부 검증
    public void validateClosable() {
        if (this == CLOSED) {
            return;
        }

        if (this != RECRUITING) {
            throw new IllegalStateException("운영 중인 클래스만 마감할 수 있습니다.");
        }
    }

    public void validateAddable() {
        if (this != RECRUITING) {
            throw new IllegalStateException("모집 중(RECRUITING) 상태에서만 세션을 추가할 수 있습니다.");
        }
    }

}
