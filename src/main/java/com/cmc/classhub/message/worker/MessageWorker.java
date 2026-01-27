package com.cmc.classhub.message.worker;

import com.cmc.classhub.message.client.MessageClient;
import com.cmc.classhub.message.client.MessageSendResult;
import com.cmc.classhub.message.domain.*;
import com.cmc.classhub.message.repository.MessageRepository;
import com.cmc.classhub.message.repository.MessageTemplateRepository;
import com.cmc.classhub.message.service.MessageRenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DB에 쌓인 PENDING 요청을 주기적으로 가져와서 실제 발송하는 워커
 * - 트랜잭션: DB 선점만 / 외부 API 호출은 트랜잭션 밖에서
 * - 재시도 정책 포함
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageWorker {

    private final MessageRepository messageRepository;
    private final MessageTemplateRepository templateRepository;
    private final MessageRenderService messageRenderService;
    private final MessageClient messageClient;

    @Value("${message.sms.batch-size:20}")
    private int batchSize;

    @Value("${message.sms.retry.max-count:5}")
    private int maxRetryCount;

    /**
     * 1분마다 실행
     */
    @Scheduled(fixedDelay = 60000)
    public void run() {
        // 1) 트랜잭션 내에서 PENDING -> SENDING 선점
        List<Long> acquiredIds = acquireMessages();

        // 2) 트랜잭션 밖에서 실제 발송 (외부 API 호출)
        for (Long messageId : acquiredIds) {
            processOne(messageId);
        }
    }

    /**
     * PENDING 메시지를 SENDING으로 선점 (트랜잭션)
     */
    @Transactional
    public List<Long> acquireMessages() {
        LocalDateTime now = LocalDateTime.now();
        List<Long> acquiredIds = new ArrayList<>();

        List<Message> due = messageRepository.findDue(
                MessageStatus.PENDING,
                now,
                PageRequest.of(0, batchSize)
        );

        for (Message msg : due) {
            int updated = messageRepository.updateStatusIfMatched(
                    msg.getId(),
                    MessageStatus.PENDING,
                    MessageStatus.SENDING
            );

            if (updated > 0) {
                acquiredIds.add(msg.getId());
            }
        }

        return acquiredIds;
    }

    /**
     * 개별 메시지 발송 처리 (외부 API 호출 포함)
     */
    private void processOne(Long messageId) {
        try {
            // 1) 데이터 조회 (트랜잭션)
            SendContext ctx = prepareSend(messageId);

            // 2) 외부 API 호출 (트랜잭션 밖)
            MessageSendResult result = messageClient.send(ctx.toPhone, ctx.renderedMessage);

            // 3) 결과 저장 (트랜잭션)
            if (result.isSuccess()) {
                markAsSent(messageId, result.getProviderMessageId());
            } else {
                handleFailure(messageId, result.getErrorMessage());
            }

        } catch (Exception e) {
            log.error("메시지 발송 실패: messageId={}", messageId, e);
            handleFailure(messageId, e.getMessage());
        }
    }

    /**
     * 발송 준비 데이터 조회 (트랜잭션)
     */
    @Transactional(readOnly = true)
    public SendContext prepareSend(Long messageId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지 없음: " + messageId));

        MessageRenderService.MessagePayload payload = messageRenderService.getPayload(msg.getReservationId());

        MessageTemplate template = templateRepository.findByType(msg.getType())
                .orElseThrow(() -> new RuntimeException("템플릿 없음: " + msg.getType()));

        String renderedMessage = messageRenderService.render(template.getBody(), payload.getVariables());

        return new SendContext(payload.getToPhone(), renderedMessage);
    }

    /**
     * 발송 성공 처리 (트랜잭션)
     */
    @Transactional
    public void markAsSent(Long messageId, String providerMessageId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지 없음: " + messageId));

        msg.markAsSent(providerMessageId);
        messageRepository.save(msg);
    }

    /**
     * 발송 실패 처리 (트랜잭션)
     */
    @Transactional
    public void handleFailure(Long messageId, String error) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지 없음: " + messageId));

        if (msg.getRetryCount() + 1 >= maxRetryCount) {
            msg.markAsFailed(error);
        } else {
            msg.scheduleRetry(error, maxRetryCount);
        }

        messageRepository.save(msg);
    }

    /**
     * 발송에 필요한 데이터 컨텍스트
     */
    private record SendContext(String toPhone, String renderedMessage) {}
}
