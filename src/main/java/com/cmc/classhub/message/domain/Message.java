package com.cmc.classhub.message.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status; // PENDING, SENT, FAILED

    private LocalDateTime scheduledAt; // 발송 예정 일시

    private LocalDateTime sentAt; // 실제 발송 일시

    private String providerMessageId; // 외부 문자 발송 서비스 메시지 ID

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Message(Long reservationId, LocalDateTime scheduledAt) {
        this.reservationId = reservationId;
        this.status = MessageStatus.PENDING;
        this.scheduledAt = scheduledAt;
        this.createdAt = LocalDateTime.now();
    }
}
