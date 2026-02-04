package com.cmc.classhub.message.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageSendResult {
    private boolean success;
    private String providerMessageId;
    private String errorMessage;
    private String failCode; // 실패 코드 추가

    public static MessageSendResult ok(String providerMessageId) {
        return new MessageSendResult(true, providerMessageId, null, null);
    }

    public static MessageSendResult fail(String errorMessage, String failCode) {
        return new MessageSendResult(false, null, errorMessage, failCode);
    }

    public static MessageSendResult fail(String errorMessage) {
        return new MessageSendResult(false, null, errorMessage, "UNKNOWN");
    }
}
