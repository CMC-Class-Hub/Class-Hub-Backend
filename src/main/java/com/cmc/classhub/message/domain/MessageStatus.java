package com.cmc.classhub.message.domain;

public enum MessageStatus {
    PENDING,   // 전송 대기
    SENDING,   // 전송 중(워커가 잡음)
    SENT,      // 전송 완료
    FAILED,    // 재시도 끝내고 실패 확정
    CANCELED   // 예약 취소 등
}
