package com.cmc.classhub.message.scheduler;

import com.cmc.classhub.message.domain.MessageTemplateType;
import com.cmc.classhub.message.service.MessageService;
import com.cmc.classhub.onedayClass.domain.Session;
import com.cmc.classhub.onedayClass.repository.SessionRepository;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.domain.ReservationStatus;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 메시지 발송 스케줄러
 * 매일 오전 9시에 D-3, D-1 리마인더 발송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageScheduler {

    private final SessionRepository sessionRepository;
    private final ReservationRepository reservationRepository;
    private final MessageService messageService;

    /**
     * 매일 오전 10시에 실행
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void sendReminders() {
        log.info("리마인더 발송 스케줄러 시작");

        LocalDate today = LocalDate.now();
        LocalDate d3Target = today.plusDays(3); // 3일 뒤 세션
        LocalDate d1Target = today.plusDays(1); // 1일 뒤 세션

        // D-3 리마인더
        sendReminderForDate(d3Target, MessageTemplateType.REMINDER_D3);

        // D-1 리마인더
        sendReminderForDate(d1Target, MessageTemplateType.REMINDER_D1);

        log.info("리마인더 발송 스케줄러 종료");
    }

    /**
     * 특정 날짜의 세션에 대해 리마인더 발송
     */
    private void sendReminderForDate(LocalDate sessionDate, MessageTemplateType templateType) {
        List<Session> sessions = sessionRepository.findByDate(sessionDate);

        if (sessions.isEmpty()) {
            log.debug("{}에 예정된 세션 없음", sessionDate);
            return;
        }

        log.info("{} 리마인더 발송 시작: {} 세션 {}개", templateType, sessionDate, sessions.size());

        for (Session session : sessions) {
            sendReminderForSession(session, templateType);
        }
    }

    /**
     * 특정 세션의 예약자들에게 리마인더 발송
     */
    private void sendReminderForSession(Session session, MessageTemplateType templateType) {
        // CONFIRMED 상태의 예약자들 조회
        List<Reservation> reservations = reservationRepository.findBySessionIdAndStatus(
                session.getId(),
                ReservationStatus.CONFIRMED);

        if (reservations.isEmpty()) {
            log.debug("세션 {} 예약자 없음", session.getId());
            return;
        }

        log.info("세션 {} 예약자 {}명에게 {} 발송", session.getId(), reservations.size(), templateType);

        // 각 예약자에게 발송
        for (Reservation reservation : reservations) {
            try {
                messageService.send(templateType, reservation.getId());
            } catch (Exception e) {
                log.error("발송 실패: reservationId={}, type={}", reservation.getId(), templateType, e);
            }
        }
    }
}
