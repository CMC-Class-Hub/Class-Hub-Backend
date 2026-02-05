package com.cmc.classhub.message.controller;

import com.cmc.classhub.message.domain.MessageTemplateType;
import com.cmc.classhub.message.dto.MessageTemplateResponse;
import com.cmc.classhub.message.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages/templates")
public class MessageTemplateController {

    private final MessageTemplateService messageTemplateService;

    // 전체 템플릿 목록 조회 (타이틀만)
    @GetMapping
    public List<String> getTemplates() {
        return messageTemplateService.getTemplates();
    }

    // 템플릿 상세 조회 (타이틀로)
    @GetMapping("/{title}")
    public MessageTemplateResponse getTemplate(@PathVariable String title) {
        return messageTemplateService.getTemplateByTitle(title);
    }
}
