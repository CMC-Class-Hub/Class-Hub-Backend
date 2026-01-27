package com.cmc.classhub.message.client.solapi;

import com.cmc.classhub.message.client.MessageClient;
import com.cmc.classhub.message.client.MessageSendResult;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.dto.response.MultipleDetailMessageSentResponse;
import com.solapi.sdk.message.service.DefaultMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * SOLAPI SMS 발송 클라이언트
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "message.sms", name = "provider", havingValue = "solapi")
public class SolapiSmsClient implements MessageClient {

    private final DefaultMessageService messageService;
    private final SolapiConfig config;

    @Override
    public MessageSendResult send(String toPhone, String text) {
        try {
            String to = toPhone;
            String from = config.getFrom();

            Message message = new Message(); // solapi의 Message 객체
            message.setFrom(from);
            message.setTo(to);
            message.setText(text);

            MultipleDetailMessageSentResponse response = messageService.send(message);

            String groupId = response.getGroupInfo().getGroupId();

            return MessageSendResult.ok(groupId);

        } catch (Exception e) {
            e.printStackTrace();  // 상세 에러 출력
            return MessageSendResult.fail(e.getMessage());
        }
    }
}
