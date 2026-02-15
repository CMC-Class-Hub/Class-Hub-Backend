package com.cmc.classhub.message.sender.reservation;

import com.cmc.classhub.message.client.MessageClient;
import com.cmc.classhub.message.domain.MessageTemplateType;
import com.cmc.classhub.message.repository.MessageRepository;
import com.cmc.classhub.onedayClass.domain.OnedayClass;
import com.cmc.classhub.onedayClass.domain.Session;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.reservation.domain.Member;
import com.cmc.classhub.reservation.repository.MemberRepository;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * D-3 리마인더 발송
 */
@Component
public class ReminderD3Sender extends ReservationSender {

    public ReminderD3Sender(
            ReservationRepository reservationRepository,
            MemberRepository memberRepository,
            OnedayClassRepository onedayClassRepository,
            MessageRepository messageRepository,
            MessageClient messageClient) {
        super(reservationRepository, memberRepository, onedayClassRepository, messageRepository, messageClient);
    }

    @Override
    public MessageTemplateType getSupportedType() {
        return MessageTemplateType.REMINDER_D3;
    }

    @Override
    protected Map<String, String> createVariables(String reservationCode, Member member, OnedayClass onedayClass, Session session) {
        Map<String, String> variables = new HashMap<>();

        variables.put("#{수강생명}", nullSafe(member.getName()));
        variables.put("#{클래스명}", nullSafe(onedayClass.getTitle()));
        variables.put("#{날짜}", session.getDate().format(DATE_FORMAT));
        variables.put("#{시간}", session.getStartTime().format(TIME_FORMAT));
        variables.put("#{장소}", nullSafe(onedayClass.getLocation()));
        variables.put("#{준비물}", nullSafe(onedayClass.getMaterial()));
        variables.put("#{주차}", nullSafe(onedayClass.getParkingInfo()));
        variables.put("#{예약코드}", reservationCode);

        return variables;
    }
}
