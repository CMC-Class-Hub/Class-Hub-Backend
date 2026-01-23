package com.cmc.classhub.reservation.service;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.service.OnedayClassService;
import com.cmc.classhub.member.domain.Member;
import com.cmc.classhub.member.repository.MemberRepository;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.dto.ReservationRequest;
import com.cmc.classhub.reservation.dto.ReservationResponse;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import java.util.List;
import java.util.stream.Collectors;
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

    public Long createReservation(ReservationRequest request, Long onedayClassId) {
        Member member = memberRepository.findByPhone(request.getPhoneNumber())
                .orElseGet(() -> {
                    return memberRepository.save(Member.builder()
                            .name(request.getApplicantName())
                            .phone(request.getPhoneNumber())
                            .email(request.getPhoneNumber() + "@guest.com") // 이메일은 필수이므로 임시값 생성
                            .loginId("guest_" + request.getPhoneNumber())   // 임시 ID
                            .password("guest1234")                          // 임시 PW
                            .build());
                });

        member.updateReservationInfo(request.getApplicantName(), request.getPhoneNumber());

        OnedayClass onedayClass = onedayClassService.findById(onedayClassId)
                .orElseThrow(() -> new IllegalArgumentException("해당 세션이 포함된 클래스를 찾을 수 없습니다."));

        onedayClass.reserveSession(request.getSessionId());

        Reservation reservation = Reservation.builder()
                .sessionId(request.getSessionId())
                .memberId(member.getId())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        return reservationRepository.save(reservation).getId();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsBySession(Long sessionId) {
        // 1. 해당 세션의 모든 예약 조회
        List<Reservation> reservations = reservationRepository.findAllBySessionId(sessionId);

        // 2. 예약 정보 + 회원 정보 매핑하여 반환
        return reservations.stream()
                .map(reservation -> {
                    Member member = memberRepository.findById(reservation.getMemberId())
                            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

                    return ReservationResponse.builder()
                            .reservationId(reservation.getId())
                            .applicantName(member.getName())
                            .phoneNumber(member.getPhone())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
