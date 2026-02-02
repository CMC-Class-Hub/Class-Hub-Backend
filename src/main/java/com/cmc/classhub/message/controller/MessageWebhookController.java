package com.cmc.classhub.message.controller;

import com.cmc.classhub.message.dto.SolapiWebhookRequest;
import com.cmc.classhub.message.service.MessageWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages/webhook")
public class MessageWebhookController {

    private final MessageWebhookService messageWebhookService;

    /**
     * Solapi 메시지 발송 결과 수신 (Webhook)
     * Solapi에서 POST로 배열 형태로 데이터를 전송함
     */
    @PostMapping
    public void receiveWebhook(@RequestBody List<SolapiWebhookRequest> requests) {
        log.info("Webhook Request Count: {}", requests.size());

        for (SolapiWebhookRequest request : requests) {
            try {
                messageWebhookService.updateStatusFromWebhook(request);
            } catch (Exception e) {
                log.error("Webhook 처리 중 오류 발생: request={}", request, e);
            }
        }
    }
}
