package com.cmc.classhub.message.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;

    private String providerMessageId; // Solapi Group ID or Message ID

    private String failCode; // 실패 코드

    @Column(columnDefinition = "TEXT")
    private String failReason; // 실패 사유

    @Column(nullable = false)
    private LocalDateTime requestedAt; // 요청 일시

    private LocalDateTime completedAt; // 완료(성공/실패) 일시

    @Builder
    private Message(DomainType domainType, Long rid, MessageTemplateType templateType,
            String receiver, MessageStatus status, String providerMessageId,
            String failReason, String failCode) {
        this.domainType = domainType;
        this.rid = rid;
        this.templateType = templateType;
        this.receiver = receiver;
        this.status = status;
        this.providerMessageId = providerMessageId;
        this.failReason = failReason;
        this.failCode = failCode;
        this.requestedAt = LocalDateTime.now();
    }

    // 발송 요청 (SENDING)
    public static Message sending(DomainType domainType, Long rid, MessageTemplateType templateType,
            String receiver, String providerMessageId) {
        return Message.builder()
                .domainType(domainType)
                .rid(rid)
                .templateType(templateType)
                .receiver(receiver)
                .status(MessageStatus.SENDING)
                .providerMessageId(providerMessageId)
                .build();
    }

    // 발송 실패 (즉시 실패)
    public static Message fail(DomainType domainType, Long rid, MessageTemplateType templateType,
            String receiver, String failReason, String failCode) {
        Message message = Message.builder()
                .domainType(domainType)
                .rid(rid)
                .templateType(templateType)
                .receiver(receiver)
                .status(MessageStatus.FAILED)
                .failReason(failReason)
                .failCode(failCode)
                .build();
        message.completedAt = LocalDateTime.now(); // 즉시 완료 처리
        return message;
    }

    // 상태 업데이트 (Webhook 등에서 호출)
    public void markAsSent() {
        this.status = MessageStatus.SENT;
        this.completedAt = LocalDateTime.now();
    }

    public void markAsFailed(String failReason, String failCode) {
        this.status = MessageStatus.FAILED;
        this.failReason = failReason;
        this.failCode = failCode;
        this.completedAt = LocalDateTime.now();
    }
}
