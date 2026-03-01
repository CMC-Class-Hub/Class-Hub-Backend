package com.cmc.classhub.message.controller;

import com.cmc.classhub.message.dto.MessageTemplateMetadata;
import com.cmc.classhub.message.dto.MessageTemplateResponse;
import com.cmc.classhub.message.service.MessageTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "MessageTemplate", description = "메시지 템플릿 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages/templates")
public class MessageTemplateController {

    private final MessageTemplateService messageTemplateService;

    @Operation(summary = "템플릿 목록 조회", description = "type 파라미터로 필터링: auto(자동발송), manual(수동발송), 미지정시 전체 조회")
    @GetMapping
    public List<MessageTemplateMetadata> getTemplates(
            @Parameter(description = "템플릿 타입 (auto, manual, 미지정시 전체)")
            @RequestParam(required = false) String type) {
        if (type == null) {
            return messageTemplateService.getTemplates();
        }
        return switch (type) {
            case "manual" -> messageTemplateService.getManualTemplates();
            case "auto" -> messageTemplateService.getAutoTemplates();
            default -> messageTemplateService.getTemplates();
        };
    }

    @Operation(summary = "템플릿 상세 조회", description = "특정 템플릿의 상세 정보를 조회합니다")
    @GetMapping("/{title}")
    public MessageTemplateResponse getTemplate(
            @Parameter(description = "템플릿 타이틀") @PathVariable String title) {
        return messageTemplateService.getTemplateByTitle(title);
    }
}
