package com.cmc.classhub.message.dto;

import com.cmc.classhub.message.domain.MessageTemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

@Schema(description = "수동 메시지 발송 요청")
public record ManualMessageRequest(
        @Schema(description = "템플릿 타입", example = "MANUAL_LOC_CHG")
        @NotNull
        MessageTemplateType templateType,

        @Schema(description = "템플릿 변수", example = "{\"#{수강생명}\": \"홍길동\", \"#{클래스명}\": \"도자기 클래스\"}")
        @NotNull
        Map<String, String> variables,

        @Schema(description = "수신자 예약 ID 목록", example = "[1, 2, 3]")
        @NotEmpty
        List<Long> reservationIds
) {
}
