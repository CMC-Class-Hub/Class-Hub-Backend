package com.cmc.classhub.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.cmc.classhub.message.domain.MessageTemplateType;

@Schema(description = "메시지 템플릿 메타데이터")
public record MessageTemplateMetadata(
        @Schema(description = "템플릿 제목", example = "예약 확인")
        String title,

        @Schema(description = "템플릿 설명", example = "예약 확인 메시지 템플릿입니다.")
        String description
){
    public static MessageTemplateMetadata from(MessageTemplateType type){
        return new MessageTemplateMetadata(type.getTitle(), type.getDescription());
    }
}
