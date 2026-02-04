package com.cmc.classhub.message.service;

import com.cmc.classhub.message.domain.MessageTemplateType;
import com.cmc.classhub.message.sender.MessageSender;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 메시지 발송 서비스
 * Map으로 템플릿 타입과 MessageSender 구현체를 연결
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final List<MessageSender> senders;
    private final Map<MessageTemplateType, MessageSender> senderMap = new EnumMap<>(MessageTemplateType.class);

    @PostConstruct
    public void init() {
        // Sender 등록
        for (MessageSender sender : senders) {
            senderMap.put(sender.getSupportedType(), sender);
            log.info("MessageSender 등록: {} -> {}", sender.getSupportedType(), sender.getClass().getSimpleName());
        }
    }

    /**
     * 템플릿 타입에 맞는 Sender로 메시지 발송
     */
    public void send(MessageTemplateType templateType, Long reservationId) {
        MessageSender sender = senderMap.get(templateType);
        if (sender == null) {
            throw new IllegalArgumentException("지원하지 않는 템플릿 타입: " + templateType);
        }
        sender.send(reservationId);
    }
}
