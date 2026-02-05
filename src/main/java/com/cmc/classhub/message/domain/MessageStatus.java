package com.cmc.classhub.message.domain;

public enum MessageStatus {
    SENDING, // 발송 요청됨 (Solapi 접수 완료)
    SENT, // 발송 성공 (수신자 도착)
    FAILED // 발송 실패
}
