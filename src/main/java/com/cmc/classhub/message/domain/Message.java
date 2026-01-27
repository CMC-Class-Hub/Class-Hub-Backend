package com.cmc.classhub.message.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

/**
 * "보낼 문자"를 DB에 저장해두는 큐
 * 상태 전이는 반드시 메서드를 통해서만 가능
 */
@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(name = "idx_messages_status_scheduled", columnList = "status, scheduledAt")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;

    @Enumerated(EnumType.STRING)
    private MessageTemplateType type;

    private LocalDateTime scheduledAt;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 150)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.PENDING;

    private String providerMessageId;

    private LocalDateTime sentAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    // ===== 생성 메서드 =====

    public static Message create(Long reservationId, MessageTemplateType type, LocalDateTime scheduledAt, String idempotencyKey) {
        Message msg = new Message();
        msg.reservationId = reservationId;
        msg.type = type;
        msg.scheduledAt = scheduledAt;
        msg.idempotencyKey = idempotencyKey;
        msg.status = MessageStatus.PENDING;
        return msg;
    }

    // ===== 상태 전이 메서드 =====

    /**
     * 발송 성공
     */
    public void markAsSent(String providerMessageId) {
        this.status = MessageStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.providerMessageId = providerMessageId;
        this.lastError = null;
    }

    /**
     * 발송 최종 실패 (재시도 소진)
     */
    public void markAsFailed(String error) {
        this.status = MessageStatus.FAILED;
        this.retryCount++;
        this.lastError = error;
    }

    /**
     * 재시도 예약
     */
    public void scheduleRetry(String error, int maxRetryCount) {
        this.retryCount++;
        this.lastError = error;

        if (this.retryCount >= maxRetryCount) {
            this.status = MessageStatus.FAILED;
            return;
        }

        // 재시도 간격: 1m -> 5m -> 30m -> 2h -> 6h
        int[] minutes = {1, 5, 30, 120, 360};
        int idx = Math.min(this.retryCount - 1, minutes.length - 1);

        this.scheduledAt = LocalDateTime.now().plusMinutes(minutes[idx]);
        this.status = MessageStatus.PENDING;
    }

    /**
     * 발송 취소
     */
    public void cancel() {
        if (this.status != MessageStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태의 메시지만 취소할 수 있습니다. 현재: " + this.status);
        }
        this.status = MessageStatus.CANCELED;
    }
}
