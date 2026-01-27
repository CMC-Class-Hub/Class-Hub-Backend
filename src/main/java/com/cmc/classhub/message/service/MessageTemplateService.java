package com.cmc.classhub.message.service;

import com.cmc.classhub.message.domain.MessageTemplate;
import com.cmc.classhub.message.domain.MessageTemplateType;
import com.cmc.classhub.message.repository.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 메시지 템플릿 조회/관리 서비스
 */
@Service
@RequiredArgsConstructor
public class MessageTemplateService {

    private final MessageTemplateRepository templateRepository;

    @Transactional(readOnly = true)
    public List<MessageTemplate> getTemplates() {
        return templateRepository.findAll();
    }
}
