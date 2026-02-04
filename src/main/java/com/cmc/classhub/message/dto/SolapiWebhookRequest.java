package com.cmc.classhub.message.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolapiWebhookRequest {
    private String messageId; // Solapi 메시지 ID
    private String groupId; // 그룹 ID
    private String statusCode; // 상태 코드 (4000: 수신 성공)
    private String statusMessage; // 상태 메시지 (수신 완료 등)
    private String to; // 수신 번호
    private String from; // 발신 번호
    private String type; // 메시지 타입 (ATA, SMS 등)
    private String dateProcessed; // 처리 일시
    private String dateReported; // 결과 보고 일시
    private String dateReceived; // 수신 일시
    private String networkCode; // 통신사 코드
}
