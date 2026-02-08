package com.cmc.classhub.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.cmc.classhub.message.domain.MessageTemplateType;

@Schema(description = "메시지 템플릿 응답")
public record MessageTemplateResponse(
        @Schema(description = "템플릿 타입", example = "RESERVATION_CONFIRM")
        MessageTemplateType type,

        @Schema(description = "템플릿 제목", example = "예약 확인")
        String title,

        @Schema(description = "템플릿 설명", example = "예약 확인 메시지 템플릿입니다.")
        String description,

        @Schema(description = "템플릿 본문", example = "#{이름}님, 예약이 확인되었습니다.")
        String body
){
    public static MessageTemplateResponse of(MessageTemplateType type, String title, String description, String body){
        return new MessageTemplateResponse(type, title, description, body);
    }
}
