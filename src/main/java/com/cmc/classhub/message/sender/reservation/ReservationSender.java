package com.cmc.classhub.message.sender.reservation;

import com.cmc.classhub.member.domain.Member;
import com.cmc.classhub.member.repository.MemberRepository;
import com.cmc.classhub.message.client.MessageClient;
import com.cmc.classhub.message.domain.DomainType;
import com.cmc.classhub.message.repository.MessageRepository;
import com.cmc.classhub.message.sender.MessageSender;
import com.cmc.classhub.onedayClass.domain.OnedayClass;
import com.cmc.classhub.onedayClass.domain.Session;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cmc.classhub.message.domain.MessageStatus;

/**
 * 예약 도메인 메시지 발송 중간 계층
 */
@Slf4j
public abstract class ReservationSender extends MessageSender {

        protected final ReservationRepository reservationRepository;
        protected final MemberRepository memberRepository;
        protected final OnedayClassRepository onedayClassRepository;

        protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        protected static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

        public ReservationSender(
                        ReservationRepository reservationRepository,
                        MemberRepository memberRepository,
                        OnedayClassRepository onedayClassRepository,
                        MessageRepository messageRepository,
                        MessageClient messageClient) {
                super(messageRepository, messageClient);
                this.reservationRepository = reservationRepository;
                this.memberRepository = memberRepository;
                this.onedayClassRepository = onedayClassRepository;
        }

        @Override
        @Transactional
        public void send(Long reservationId) {
                // 1. 데이터 조회
                Reservation reservation = reservationRepository.findById(reservationId)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다. id=" + reservationId));

                // 중복 발송 체크
                if (isDuplicateSend(reservationId)) {
                        log.debug("이미 발송됨: reservationId={}, type={}", reservationId, getSupportedType());
                        return;
                }

                Member member = memberRepository.findById(reservation.getMemberId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "존재하지 않는 회원입니다. id=" + reservation.getMemberId()));

                OnedayClass onedayClass = onedayClassRepository.findBySessionsId(reservation.getSessionId())
                                .orElseThrow(
                                                () -> new IllegalArgumentException("클래스를 찾을 수 없습니다. sessionId="
                                                                + reservation.getSessionId()));

                Session session = onedayClass.getSessions().stream()
                                .filter(s -> s.getId().equals(reservation.getSessionId()))
                                .findFirst()
                                .orElseThrow(
                                                () -> new IllegalArgumentException("세션을 찾을 수 없습니다. sessionId="
                                                                + reservation.getSessionId()));

                // 2. 변수 생성 (자식 클래스에게 위임)
                Map<String, String> variables = createVariables(member, onedayClass, session);

                // 3. 발송 (부모 클래스의 공통 발송 로직 호출)
                super.sendMessage(reservationId, DomainType.RESERVATION, member.getPhone(), variables);
        }

        /**
         * [Abstract Method] 각 메시지 타입별로 필요한 변수를 생성
         */
        protected abstract Map<String, String> createVariables(Member member, OnedayClass onedayClass, Session session);

        /**
         * 중복 확인 메서드
         */
        private boolean isDuplicateSend(Long reservationId) {
                return messageRepository.existsByDomainTypeAndRidAndTemplateTypeAndStatusIn(
                                DomainType.RESERVATION,
                                reservationId,
                                getSupportedType(),
                                List.of(MessageStatus.SENT, MessageStatus.SENDING));
        }
}
