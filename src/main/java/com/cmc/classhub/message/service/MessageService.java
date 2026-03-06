package com.cmc.classhub.message.service;

import com.cmc.classhub.message.client.MessageClient;
import com.cmc.classhub.message.client.MessageSendResult;
import com.cmc.classhub.message.domain.DomainType;
import com.cmc.classhub.message.domain.Message;
import com.cmc.classhub.message.domain.MessageTemplateType;
import com.cmc.classhub.message.dto.ManualMessageRequest;
import com.cmc.classhub.message.dto.MessageHistoryResponse;
import com.cmc.classhub.message.repository.MessageRepository;
import com.cmc.classhub.message.sender.MessageSender;
import com.cmc.classhub.reservation.domain.Member;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.HashMap;
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
    private final MessageRepository messageRepository; // manual 분리 필요
    private final MessageClient messageClient; // manual 분리 필요
    private final ReservationRepository reservationRepository; // manual 분리 필요

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
     * 자동 발송 (AUTO 템플릿용)
     */
    public void sendAuto(MessageTemplateType templateType, Long reservationId) {
        MessageSender sender = senderMap.get(templateType);
        if (sender == null) {
            throw new IllegalArgumentException("지원하지 않는 AUTO 템플릿 타입: " + templateType);
        }
        sender.send(reservationId);
    }

    /**
     * 수동 발송 (MANUAL 템플릿용) -> sender 분리 필요
     */
    @Transactional
    public int sendManual(ManualMessageRequest request, Long senderId) {
        List<Reservation> reservations = reservationRepository.findAllById(request.reservationIds());
        MessageTemplateType templateType = request.templateType();

        int successCount = 0;

        for (Reservation reservation : reservations) {
            Member member = reservation.getMember();
            String receiverPhone = member.getPhone();
            String receiverName = member.getName();

            Map<String, String> variables = new HashMap<>(request.variables());
            variables.putIfAbsent("#{수강생명}", receiverName);

            MessageSendResult result = messageClient.sendWithTemplate(
                    receiverPhone,
                    templateType.getTemplateId(),
                    variables
            );

            Message message;
            if (result.isSuccess()) {
                message = Message.sending(
                        DomainType.RESERVATION,
                        reservation.getId(),
                        templateType,
                        receiverName,
                        receiverPhone,
                        result.getProviderMessageId(),
                        senderId
                );
                successCount++;
                log.info("[MANUAL] 발송 성공: reservationId={}, type={}, senderId={}",
                        reservation.getId(), templateType, senderId);
            } else {
                message = Message.fail(
                        DomainType.RESERVATION,
                        reservation.getId(),
                        templateType,
                        receiverName,
                        receiverPhone,
                        result.getErrorMessage(),
                        result.getFailCode(),
                        senderId
                );
                log.warn("[MANUAL] 발송 실패: reservationId={}, error={}",
                        reservation.getId(), result.getErrorMessage());
            }

            messageRepository.save(message);
        }

        return successCount;
    }

    /**
     * 발신자별 메시지 이력 조회
     */
    @Transactional(readOnly = true)
    public List<MessageHistoryResponse> getHistoryBySenderId(Long senderId) {
        return messageRepository.findBySenderIdOrderByRequestedAtDesc(senderId).stream()
                .map(MessageHistoryResponse::from)
                .toList();
    }
}
