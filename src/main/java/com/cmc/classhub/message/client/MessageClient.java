package com.cmc.classhub.message.client;

import java.util.Map;

/**
 * 메시지 발송 인터페이스
 */
public interface MessageClient {

    /**
     * 알림톡 발송 (템플릿 기반)
     */
    MessageSendResult sendWithTemplate(String receiver, String templateId, Map<String, String> variables);
}
