package com.cmc.classhub.message.controller;

import com.cmc.classhub.message.dto.MessageTemplateResponse;
import com.cmc.classhub.message.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageTemplateService messageTemplateService;

    // 전체 템플릿 목록 조회
    @GetMapping("/templates")
    public List<MessageTemplateResponse> getTemplates() {
        return messageTemplateService.getTemplates();
    }
}
