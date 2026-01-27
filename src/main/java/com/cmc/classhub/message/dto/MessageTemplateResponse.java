package com.cmc.classhub.message.dto;

import com.cmc.classhub.message.domain.MessageTemplate;
import com.cmc.classhub.message.domain.MessageTemplateType;

public record MessageTemplateResponse(
        MessageTemplateType type,
        String title,
        String body
) {
    public static MessageTemplateResponse from(MessageTemplate template) {
        return new MessageTemplateResponse(
                template.getType(),
                template.getTitle(),
                template.getBody()
        );
    }
}
