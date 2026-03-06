package com.cmc.classhub.message.controller;

import com.cmc.classhub.message.dto.ManualMessageRequest;
import com.cmc.classhub.message.dto.MessageHistoryResponse;
import com.cmc.classhub.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Message", description = "메시지 발송 및 이력 조회 API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "수동 메시지 발송", description = "강사가 선택한 예약자들에게 메시지를 발송합니다")
    @PostMapping
    public ResponseEntity<Map<String, Object>> sendManualMessage(
            @AuthenticationPrincipal Long senderId,
            @RequestBody @Valid ManualMessageRequest request) {
        int successCount = messageService.sendManual(request, senderId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "sentCount", successCount,
                "totalCount", request.reservationIds().size()
        ));
    }

    @Operation(summary = "내가 보낸 메시지 이력 조회", description = "로그인한 강사가 발송한 메시지 이력을 조회합니다")
    @GetMapping("/history")
    public ResponseEntity<List<MessageHistoryResponse>> getMyMessageHistory(
            @AuthenticationPrincipal Long senderId) {
        return ResponseEntity.ok(messageService.getHistoryBySenderId(senderId));
    }
}
