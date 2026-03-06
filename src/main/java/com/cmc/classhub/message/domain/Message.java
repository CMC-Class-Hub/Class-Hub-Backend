package com.cmc.classhub.message.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 발송된 메시지 이력 (독립 테이블)
 */
@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DomainType domainType;

    @Column(nullable = false)
    private Long rid; // Reference ID (연관관계 없이 참조만)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageTemplateType templateType;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String receiverPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;

    private Long senderId; // 발신자 ID (강사 ID, 자동 발송이면 null)

    private String providerMessageId; // Solapi Group ID or Message ID

    private String failCode; // 실패 코드

    @Column(columnDefinition = "TEXT")
    private String failReason; // 실패 사유

    @Column(nullable = false)
    private LocalDateTime requestedAt; // 요청 일시

    private LocalDateTime completedAt; // 완료(성공/실패) 일시

    @Column(columnDefinition = "TEXT")
    private String content; // 실제 발송된 메시지 내용

    @Builder
    private Message(DomainType domainType, Long rid, MessageTemplateType templateType,
            String receiverName, String receiverPhone, MessageStatus status, String providerMessageId,
            String failReason, String failCode, Long senderId) {
        this.domainType = domainType;
        this.rid = rid;
        this.templateType = templateType;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.status = status;
        this.providerMessageId = providerMessageId;
        this.failReason = failReason;
        this.failCode = failCode;
        this.senderId = senderId;
        this.requestedAt = LocalDateTime.now();
    }

    // 발송 요청 (SENDING) - 자동 발송용
    public static Message sending(DomainType domainType, Long rid, MessageTemplateType templateType,
            String receiverName, String receiverPhone, String providerMessageId) {
        return sending(domainType, rid, templateType, receiverName, receiverPhone, providerMessageId, null);
    }

    // 발송 요청 (SENDING) - 수동 발송용 (senderId 포함)
    public static Message sending(DomainType domainType, Long rid, MessageTemplateType templateType,
            String receiverName, String receiverPhone, String providerMessageId, Long senderId) {
        return Message.builder()
                .domainType(domainType)
                .rid(rid)
                .templateType(templateType)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .status(MessageStatus.SENDING)
                .providerMessageId(providerMessageId)
                .senderId(senderId)
                .build();
    }

    // 발송 실패 (즉시 실패) - 자동 발송용
    public static Message fail(DomainType domainType, Long rid, MessageTemplateType templateType,
            String receiverName, String receiverPhone, String failReason, String failCode) {
        return fail(domainType, rid, templateType, receiverName, receiverPhone, failReason, failCode, null);
    }

    // 발송 실패 (즉시 실패) - 수동 발송용 (senderId 포함)
    public static Message fail(DomainType domainType, Long rid, MessageTemplateType templateType,
            String receiverName, String receiverPhone, String failReason, String failCode, Long senderId) {
        Message message = Message.builder()
                .domainType(domainType)
                .rid(rid)
                .templateType(templateType)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .status(MessageStatus.FAILED)
                .failReason(failReason)
                .failCode(failCode)
                .senderId(senderId)
                .build();
        message.completedAt = LocalDateTime.now();
        return message;
    }

    // 상태 업데이트 (Webhook - Timestamp 사용)
    public void markAsSent(String dateString) {
        this.status = MessageStatus.SENT;
        if (dateString != null) {
            try {
                // ISO 8601 (UTC) -> System Default Zone (KST) 변환
                // 예: 02:18Z -> 11:18 (KST)
                this.completedAt = ZonedDateTime.parse(dateString)
                        .withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime();
            } catch (Exception e) {
                // 파싱 실패 시 현재 시간으로 대체 (안전장치)
                this.completedAt = LocalDateTime.now();
            }
        } else {
            this.completedAt = LocalDateTime.now();
        }
    }

    public void markAsFailed(String failReason, String failCode) {
        this.status = MessageStatus.FAILED;
        this.failReason = failReason;
        this.failCode = failCode;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
