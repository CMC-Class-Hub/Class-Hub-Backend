/*** 

package com.cmc.classhub.message.controller;

import com.cmc.classhub.message.domain.MessageTemplateVariable;
import com.cmc.classhub.message.dto.MessageTemplateResponse;
import com.cmc.classhub.message.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageTemplateService messageTemplateService;

    /**
     * 템플릿에서 사용 가능한 변수 목록 조회
     */
    /* 
    @GetMapping("/template-variables")
    public List<String> getTemplateVariables() {
        return Arrays.stream(MessageTemplateVariable.values())
                .map(MessageTemplateVariable::getKey)
                .toList();
    }

    /**
     * 전체 템플릿 목록 조회
     */
    /* 
    @GetMapping("/templates")
    public List<MessageTemplateResponse> getTemplates() {
        return messageTemplateService.getTemplates().stream()
                .map(MessageTemplateResponse::from)
                .toList();
    }
}
 */