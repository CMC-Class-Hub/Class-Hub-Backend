package com.cmc.classhub.message.service;

import com.cmc.classhub.message.domain.Message;
import com.cmc.classhub.message.dto.SolapiWebhookRequest;
import com.cmc.classhub.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageWebhookService {

    private final MessageRepository messageRepository;

    /**
     * Webhook 수신하여 메시지 상태 업데이트
     */
    @Transactional
    public void updateStatusFromWebhook(SolapiWebhookRequest request) {
        log.info("Webhook received: {}", request);

        if (request.getMessageId() == null) {
            log.warn("Webhook messageId is null");
            return;
        }

        Message message = messageRepository.findByProviderMessageId(request.getMessageId())
                .orElse(null);

        if (message == null) {
            log.warn("메시지를 찾을 수 없음: messageId={}", request.getMessageId());
            return;
        }

        // Solapi 상태 코드가 4000(수신 성공)이면 성공 처리
        if ("4000".equals(request.getStatusCode())) {
            // 수신 일시(dateReceived)를 완료 일시로 저장 (없으면 현재 시간)
            message.markAsSent(request.getDateReceived());
            return;
        }

        // 그 외에는 실패로 간주 (필요시 문서 확인하여 성공 코드 추가)
        // statusCode가 null이거나 4000이 아닌 경우
        message.markAsFailed(request.getStatusMessage(), request.getStatusCode());
    }
}
