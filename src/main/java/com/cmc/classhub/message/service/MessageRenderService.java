package com.cmc.classhub.message.service;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.domain.Session;
import com.cmc.classhub.OnedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.member.domain.Member;
import com.cmc.classhub.member.repository.MemberRepository;
import com.cmc.classhub.message.domain.MessageTemplateVariable;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 메시지 발송을 위한 데이터 조회 및 템플릿 렌더링
 */
@Service
@RequiredArgsConstructor
public class MessageRenderService {

        private final ReservationRepository reservationRepository;
        private final MemberRepository memberRepository;
        private final OnedayClassRepository onedayClassRepository;

        private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

        private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{([^{}]+)}");

        /**
         * reservationId로 템플릿 변수 Map 생성
         */
        @Transactional(readOnly = true)
        public MessagePayload getPayload(Long reservationId) {
                // 1) 예약 조회
                Reservation reservation = reservationRepository.findById(reservationId)
                                .orElseThrow(() -> new RuntimeException("예약 없음: " + reservationId));

                // 2) 수강생 조회
                Member member = memberRepository.findById(reservation.getMemberId())
                                .orElseThrow(() -> new RuntimeException("회원 없음: " + reservation.getMemberId()));

                // 3) 클래스 + 세션 조회
                OnedayClass onedayClass = onedayClassRepository.findBySessionsId(reservation.getSessionId())
                                .orElseThrow(() -> new RuntimeException(
                                                "클래스 없음 (sessionId: " + reservation.getSessionId() + ")"));

                Session session = onedayClass.getSessions().stream()
                                .filter(s -> s.getId().equals(reservation.getSessionId()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("세션 없음: " + reservation.getSessionId()));

                // 4) 템플릿 변수 Map 생성 (TemplateVariable enum 사용)
                Map<MessageTemplateVariable, Object> variables = new EnumMap<>(MessageTemplateVariable.class);
                variables.put(MessageTemplateVariable.STUDENT_NAME, member.getName());
                variables.put(MessageTemplateVariable.CLASS_NAME, onedayClass.getTitle());
                variables.put(MessageTemplateVariable.DATE, session.getDate().format(DATE_FORMAT));
                variables.put(MessageTemplateVariable.TIME, session.getStartTime().format(TIME_FORMAT));
                variables.put(MessageTemplateVariable.LOCATION,
                                onedayClass.getLocation() != null ? onedayClass.getLocation() : "");
                variables.put(MessageTemplateVariable.MATERIALS,
                                onedayClass.getMaterial() != null ? onedayClass.getMaterial() : "");
                variables.put(MessageTemplateVariable.PARKING,
                                onedayClass.getParkingInfo() != null ? onedayClass.getParkingInfo() : "");
                variables.put(MessageTemplateVariable.CLASS_LINK,
                                "https://classhub-link.vercel.app/class/" + onedayClass.getShareCode());

                return new MessagePayload(
                                member.getPhone(),
                                variables);
        }

        /**
         * 템플릿 문자열의 {key}를 payload 값으로 치환
         * - payload에 없는 변수는 ""(빈 문자열)로 처리
         * => 강사가 변수 일부를 빼도 발송이 깨지지 않음
         */
        public String render(String templateBody, Map<MessageTemplateVariable, Object> variables) {
                if (templateBody == null)
                        return "";

                Matcher matcher = TOKEN_PATTERN.matcher(templateBody);
                StringBuffer sb = new StringBuffer();

                while (matcher.find()) {
                        String keyStr = matcher.group(1).trim(); // 중괄호 내부 key (예: "수강생명")

                        // key 문자열로 TemplateVariable 찾기
                        Object value = Arrays.stream(MessageTemplateVariable.values())
                                        .filter(v -> v.getKey().equals(keyStr))
                                        .findFirst()
                                        .map(variables::get)
                                        .orElse(null);

                        String replacement = (value == null) ? "" : String.valueOf(value);
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                }

                matcher.appendTail(sb);
                return sb.toString();
        }

        @Getter
        @RequiredArgsConstructor
        public static class MessagePayload {
                private final String toPhone;
                private final Map<MessageTemplateVariable, Object> variables;
        }
}
