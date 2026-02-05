package com.cmc.classhub.message.sender;

import com.cmc.classhub.message.client.MessageClient;
import com.cmc.classhub.message.client.MessageSendResult;
import com.cmc.classhub.message.domain.DomainType;
import com.cmc.classhub.message.domain.Message;
import com.cmc.classhub.message.domain.MessageTemplateType;
import com.cmc.classhub.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 메시지 발송 최상위 추상 클래스
 * - 공통 발송 로직 및 로깅 담당
 * - 도메인별 구현체는 이 클래스를 상속받아 구현
 */
@Slf4j
@RequiredArgsConstructor
public abstract class MessageSender {

    protected final MessageRepository messageRepository;
    protected final MessageClient messageClient;

    /**
     * 지원하는 템플릿 타입
     */
    public abstract MessageTemplateType getSupportedType();

    /**
     * 메시지 발송 (도메인별로 필요한 ID를 받아서 처리)
     * @param rId (예: reservationId)
     */
    public abstract void send(Long rId);

    /**
     * 공통 발송 및 이력 저장 로직
     */
    protected void sendMessage(Long rid, DomainType domainType, String receiver, Map<String, String> variables) {
        MessageTemplateType type = getSupportedType();

        // 1. 발송
        MessageSendResult result = messageClient.sendWithTemplate(receiver, type.getTemplateId(), variables);

        // 2. 이력 저장
        Message message;
        if (result.isSuccess()) {
            message = Message.sending(
                    domainType,
                    rid,
                    type,
                    receiver,
                    result.getProviderMessageId());
            log.info("발송 성공: rid={}, type={}, msgId={}", rid, type, result.getProviderMessageId());
        } else {
            message = Message.fail(
                    domainType,
                    rid,
                    type,
                    receiver,
                    result.getErrorMessage(),
                    result.getFailCode());
            log.warn("발송 실패: rid={}, type={}, error={}", rid, type, result.getErrorMessage());
        }

        messageRepository.save(message);
    }

    /**
     * Null-safe 유틸리티
     */
    protected String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
