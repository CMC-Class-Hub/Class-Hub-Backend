package com.cmc.classhub.global.init;

import com.cmc.classhub.message.domain.SystemMessageTemplate;
import com.cmc.classhub.message.domain.MessageType;
import com.cmc.classhub.message.repository.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 애플리케이션 시작 시 메시지 템플릿 초기 데이터 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageTemplateDataLoader implements CommandLineRunner {

    private final MessageTemplateRepository templateRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception  {
        initTemplate(
                MessageType.APPLY_CONFIRMED,
                "신청 완료 안내",
                """
                [Class Hub] 신청 완료 안내

                안녕하세요, {수강생명}님!
                {클래스명} 수업 신청이 완료되었습니다.

                ▶ 일시: {날짜} {시간}
                ▶ 장소: {장소}

                감사합니다.
                """
        );

        initTemplate(
                MessageType.REMINDER_D3,
                "수업 3일 전 리마인더",
                """
                [Class Hub] 수업 3일 전 안내

                안녕하세요, {수강생명}님!
                신청하신 수업이 3일 후에 진행됩니다.

                ▶ 수업명: {클래스명}
                ▶ 일시: {날짜} {시간}
                ▶ 장소: {장소}

                감사합니다.
                """
        );

        initTemplate(
                MessageType.REMINDER_D1,
                "수업 1일 전 리마인더",
                """
                [Class Hub] 수업 하루 전 안내

                안녕하세요, {수강생명}님!
                신청하신 수업이 내일 진행됩니다.

                ▶ 수업명: {클래스명}
                ▶ 일시: {날짜} {시간}
                ▶ 장소: {장소}
                ▶ 준비물: {준비물}
                ▶ 주차: {주차}

                감사합니다.
                """
        );

        log.info("메시지 템플릿 초기화 완료");
    }

    private void initTemplate(MessageType type, String title, String body) {
        if (templateRepository.findByType(type).isEmpty()) {
            SystemMessageTemplate template = SystemMessageTemplate.create(type, title, body);
            templateRepository.save(template);
            log.info("템플릿 생성: {}", type);
        }
    }
}
