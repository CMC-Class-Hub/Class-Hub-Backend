package com.cmc.classhub.message.service;

import com.cmc.classhub.message.domain.MessageTemplateType;
import com.cmc.classhub.message.dto.MessageTemplateResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 메시지 템플릿 조회 및 캐싱 서비스
 */
@Slf4j
@Service
public class MessageTemplateService {

    private static final String TEMPLATE_PATH = "message-templates/";
    private final Map<MessageTemplateType, String> templateCache = new EnumMap<>(MessageTemplateType.class);

    @PostConstruct
    public void init() {
        // 템플릿 캐싱
        for (MessageTemplateType type : MessageTemplateType.values()) {
            try {
                String body = loadTemplateFile(type);
                templateCache.put(type, body);
            } catch (Exception e) {
                log.error("필수 템플릿 로드 실패: {}", type, e);
                // 템플릿 로드 실패 시 서버 기동 중단 (필수 리소스)
                throw new IllegalStateException("필수 템플릿 로드 실패: " + type, e);
            }
        }
        log.info("모든 메시지 템플릿 캐싱 완료 ({}개)", templateCache.size());
    }

    // 모든 템플릿 목록 조회
    public List<MessageTemplateResponse> getTemplates() {
        return Arrays.stream(MessageTemplateType.values())
                .map(this::getTemplate)
                .collect(Collectors.toList());
    }

    // 특정 템플릿 조회 (캐시 사용)
    public MessageTemplateResponse getTemplate(MessageTemplateType type) {
        String body = templateCache.get(type);
        if (body == null) {
            throw new IllegalStateException("템플릿을 찾을 수 없습니다: " + type);
        }
        String title = type.getDescription();
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
            throw new RuntimeException("파일 읽기 실패", e);
        }
    }
}
