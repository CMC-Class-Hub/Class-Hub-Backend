package com.cmc.classhub.message.dto;

import com.cmc.classhub.message.domain.DomainType;
import com.cmc.classhub.message.domain.Message;
import com.cmc.classhub.message.domain.MessageStatus;
import com.cmc.classhub.message.domain.MessageTemplateType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "메시지 발송 이력 응답")
public record MessageHistoryResponse(
        @Schema(description = "메시지 ID")
        Long id,

        @Schema(description = "도메인 타입", example = "RESERVATION")
        DomainType domainType,

        @Schema(description = "참조 ID (예약 ID 등)")
        Long referenceId,

        @Schema(description = "템플릿 타입", example = "APPLY_CONFIRMED")
        MessageTemplateType templateType,

        @Schema(description = "템플릿 제목", example = "예약 완료 안내")
        String templateTitle,

        @Schema(description = "수신자 이름", example = "홍길동")
        String receiverName,

        @Schema(description = "수신자 전화번호", example = "010-1234-5678")
        String receiverPhone,

        @Schema(description = "발신자 ID (강사 ID, 자동 발송이면 null)")
        Long senderId,

        @Schema(description = "발송 상태", example = "SENT")
        MessageStatus status,

        @Schema(description = "요청 일시")
        LocalDateTime requestedAt,

        @Schema(description = "완료 일시")
        LocalDateTime completedAt,

        @Schema(description = "실패 사유")
        String failReason,

        @Schema(description = "메시지 내용")
        String content
) {
    public static MessageHistoryResponse from(Message message) {
        return new MessageHistoryResponse(
                message.getId(),
                message.getDomainType(),
                message.getRid(),
                message.getTemplateType(),
                message.getTemplateType().getTitle(),
                message.getReceiverName(),
                message.getReceiverPhone(),
                message.getSenderId(),
                message.getStatus(),
                message.getRequestedAt(),
                message.getCompletedAt(),
                message.getFailReason(),
                message.getContent()
        );
    }
}
