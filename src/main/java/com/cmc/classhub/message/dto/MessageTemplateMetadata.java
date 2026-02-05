package com.cmc.classhub.message.dto;

import com.cmc.classhub.message.domain.MessageTemplateType;

public record MessageTemplateMetadata(
        String title,
        String description
){
    public static MessageTemplateMetadata from(MessageTemplateType type){
        return new MessageTemplateMetadata(type.getTitle(),type.getDescription());
    }
}
