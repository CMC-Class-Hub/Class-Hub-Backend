package com.cmc.classhub.message.controller;

import com.cmc.classhub.message.domain.MessageTemplateType;
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

    @Operation(summary = "템플릿 목록 조회", description = "전체 메시지 템플릿 목록을 조회합니다")
    @GetMapping
    public List<MessageTemplateMetadata> getTemplates() {
        return messageTemplateService.getTemplates();
    }

    @Operation(summary = "템플릿 상세 조회", description = "특정 템플릿의 상세 정보를 조회합니다")
    @GetMapping("/{title}")
    public MessageTemplateResponse getTemplate(
            @Parameter(description = "템플릿 타이틀") @PathVariable String title) {
        return messageTemplateService.getTemplateByTitle(title);
    }
}
