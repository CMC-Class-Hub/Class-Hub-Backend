package com.cmc.classhub.message.service;

import com.cmc.classhub.message.domain.MessageTemplateType;
import com.cmc.classhub.message.dto.MessageTemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// 메시지 템플릿 조회 서비스 (파일 기반)
@Slf4j
@Service
public class MessageTemplateService {

    private static final String TEMPLATE_PATH = "message-templates/";

    // 모든 템플릿 목록 조회
    public List<MessageTemplateResponse> getTemplates() {
        return Arrays.stream(MessageTemplateType.values())
                .map(this::getTemplate)
                .collect(Collectors.toList());
    }

    // 특정 템플릿 조회
    public MessageTemplateResponse getTemplate(MessageTemplateType type) {
        String body = loadTemplateFile(type);
        String title = getTitleForType(type);
        return MessageTemplateResponse.of(type, title, body);
    }

    // 템플릿 파일 로드
    private String loadTemplateFile(MessageTemplateType type) {
        try {
            ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH + type.name() + ".txt");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            log.warn("템플릿 파일 로드 실패: {}", type, e);
            return "";
        }
    }

    // 템플릿 타입별 제목
    private String getTitleForType(MessageTemplateType type) {
        return switch (type) {
            case APPLY_CONFIRMED -> "예약 완료 안내";
            case REMINDER_D3 -> "D-3 리마인더";
            case REMINDER_D1 -> "D-1 리마인더";
        };
    }
}
