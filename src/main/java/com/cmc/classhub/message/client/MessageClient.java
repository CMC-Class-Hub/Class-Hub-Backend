package com.cmc.classhub.message.client;

/**
 * SMS 발송 인터페이스
 */
public interface MessageClient {
    MessageSendResult send(String toPhone, String message);
}
