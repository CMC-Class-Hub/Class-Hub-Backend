package com.cmc.classhub.message.client.console;

import com.cmc.classhub.message.client.MessageClient;
import com.cmc.classhub.message.client.MessageSendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "message.kakao", name = "provider", havingValue = "console", matchIfMissing = true)
public class ConsoleClient implements MessageClient {

    @Override
    public MessageSendResult sendWithTemplate(String receiver, String templateId, Map<String, String> variables) {
        String fakeId = "console-" + UUID.randomUUID();

        log.info("[ALIMTOK:CONSOLE] sendAt={}, to={}, templateId={}, variables={}, providerMessageId={}",
                LocalDateTime.now(), receiver, templateId, variables, fakeId);

        return MessageSendResult.ok(fakeId);
    }
}