package com.cmc.classhub.reservation.service;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.service.OnedayClassService;
import com.cmc.classhub.member.domain.Member;
import com.cmc.classhub.member.repository.MemberRepository;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.dto.ReservationRequest;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final OnedayClassService onedayClassService;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    public Long createReservation(ReservationRequest request, Long onedayClassId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.updateReservationInfo(request.getApplicantName(), request.getPhoneNumber());

        OnedayClass onedayClass = onedayClassService.findById(onedayClassId)
                .orElseThrow(() -> new IllegalArgumentException("해당 세션이 포함된 클래스를 찾을 수 없습니다."));

        onedayClass.reserveSession(request.getSessionId());

        Reservation reservation = Reservation.builder()
                .sessionId(request.getSessionId())
                .memberId(memberId)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        return reservationRepository.save(reservation).getId();
    }
}
