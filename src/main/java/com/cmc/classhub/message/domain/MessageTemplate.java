package com.cmc.classhub.message.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 문자 템플릿(치환 방식)
 */
@Entity
@Table(name = "message_template")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private MessageTemplateType type;

    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static MessageTemplate create(MessageTemplateType type, String title, String body) {
        MessageTemplateVariable.validateTemplate(body);

        MessageTemplate t = new MessageTemplate();
        t.type = type;
        t.title = title;
        t.body = body;
        t.createdAt = LocalDateTime.now();
        return t;
    }

    public void update(String title, String body) {
        MessageTemplateVariable.validateTemplate(body);

        this.title = title;
        this.body = body;
        this.updatedAt = LocalDateTime.now();
    }
}
