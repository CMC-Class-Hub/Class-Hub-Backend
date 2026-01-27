package com.cmc.classhub.message.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "message.sms", name = "provider", havingValue = "console", matchIfMissing = true)
public class ConsoleSmsClient implements MessageClient {

    @Override
    public MessageSendResult send(String toPhone, String message) {
        String fakeId = "console-" + UUID.randomUUID();

        log.info("[SMS:CONSOLE] sendAt={}, to={}, message={}, providerMessageId={}",
                LocalDateTime.now(), toPhone, message, fakeId);

        return MessageSendResult.ok(fakeId);
    }
}