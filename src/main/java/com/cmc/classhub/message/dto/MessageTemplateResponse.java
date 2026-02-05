package com.cmc.classhub.message.dto;

import com.cmc.classhub.message.domain.MessageTemplateType;

public record MessageTemplateResponse(
        MessageTemplateType type,
        String title,
        String body
) {
    public static MessageTemplateResponse of(MessageTemplateType type, String title, String body) {
        return new MessageTemplateResponse(type, title, body);
    }
}
