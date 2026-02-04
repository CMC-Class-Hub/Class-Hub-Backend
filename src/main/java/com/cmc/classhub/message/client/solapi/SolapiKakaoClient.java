package com.cmc.classhub.message.client.solapi;

import com.cmc.classhub.message.client.MessageClient;
import com.cmc.classhub.message.client.MessageSendResult;
import com.solapi.sdk.message.model.kakao.KakaoOption;
import com.solapi.sdk.message.model.FailedMessage;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.dto.response.MultipleDetailMessageSentResponse;
import com.solapi.sdk.message.service.DefaultMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SOLAPI 카카오 알림톡 발송 클라이언트
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "message.kakao", name = "provider", havingValue = "solapi")
public class SolapiKakaoClient implements MessageClient {

    private final DefaultMessageService messageService;
    private final SolapiKakaoConfig config;

    @Override
    public MessageSendResult sendWithTemplate(String receiver, String templateId, Map<String, String> variables) {
        try {
            Message message = new Message();
            message.setTo(receiver);
            message.setFrom(config.getFrom());

            KakaoOption kakaoOption = new KakaoOption();
            kakaoOption.setPfId(config.getPfId());
            kakaoOption.setTemplateId(templateId);
            kakaoOption.setVariables(variables);

            message.setKakaoOptions(kakaoOption);

            MultipleDetailMessageSentResponse response = messageService.send(message);

            // 실패 건 확인
            if (response.getFailedMessageList() != null && !response.getFailedMessageList().isEmpty()) {
                FailedMessage fail = response.getFailedMessageList().get(0);
                return MessageSendResult.fail(fail.getStatusMessage(), fail.getStatusCode());
            }

            String groupId = response.getGroupInfo().getGroupId();

            return MessageSendResult.ok(groupId);

        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 에러 코드는 Exception 클래스 이름으로 대체하거나 상세 파싱 필요
            return MessageSendResult.fail(e.getMessage(), e.getClass().getSimpleName());
        }
    }
}
