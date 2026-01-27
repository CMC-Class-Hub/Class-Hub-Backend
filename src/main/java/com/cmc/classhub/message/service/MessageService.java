package com.cmc.classhub.message.service;

import com.cmc.classhub.message.domain.*;
import com.cmc.classhub.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 문자 발송 요청을 DB에 쌓는 서비스
 * 실제 발송은 Worker가 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    @Transactional
    public void enqueueMessages(Long reservationId, LocalDateTime classStartAt) {
        // 1) 예약 확정 즉시 발송
        enqueueConfirmation(reservationId);

        // 2) D-3 / D-1 리마인더 예약
        enqueueReminders(reservationId, classStartAt);
    }

    /**
     * 예약 확정 문자 즉시 발송
     */
    private void enqueueConfirmation(Long reservationId) {
        enqueue(
                MessageTemplateType.APPLY_CONFIRMED,
                reservationId,
                LocalDateTime.now(),
                "APPLY_CONFIRMED:reservation:" + reservationId
        );
    }

    /**
     * 예약 확정 시 D-3 / D-1 리마인더 예약
     */
    private void enqueueReminders(Long reservationId, LocalDateTime classStartAt) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime d3 = classStartAt.minusHours(72);
        if (d3.isAfter(now)) {
            enqueue(
                    MessageTemplateType.REMINDER_D3,
                    reservationId,
                    d3,
                    "REMINDER_D3:reservation:" + reservationId
            );
        }

        LocalDateTime d1 = classStartAt.minusHours(24);
        if (d1.isAfter(now)) {
            enqueue(
                    MessageTemplateType.REMINDER_D1,
                    reservationId,
                    d1,
                    "REMINDER_D1:reservation:" + reservationId
            );
        }
    }

    private void enqueue(
            MessageTemplateType type,
            Long reservationId,
            LocalDateTime scheduledAt,
            String idempotencyKey
    ) {
        Message msg = Message.create(reservationId, type, scheduledAt, idempotencyKey);

        try {
            messageRepository.save(msg);
        } catch (DataIntegrityViolationException dup) {
            log.debug("중복 메시지 요청 무시: reservationId={}, type={}", reservationId, type);
        }
    }
}
