package com.cmc.classhub.message.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageSendResult {
    private boolean success;
    private String providerMessageId;
    private String errorMessage;

    public static MessageSendResult ok(String providerMessageId) {
        return new MessageSendResult(true, providerMessageId, null);
    }

    public static MessageSendResult fail(String errorMessage) {
        return new MessageSendResult(false, null, errorMessage);
    }
}
