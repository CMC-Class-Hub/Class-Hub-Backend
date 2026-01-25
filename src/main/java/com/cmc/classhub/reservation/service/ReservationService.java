package com.cmc.classhub.reservation.service;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.domain.Session;
import com.cmc.classhub.OnedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.OnedayClass.service.OnedayClassService;
import com.cmc.classhub.member.domain.Member;
import com.cmc.classhub.member.repository.MemberRepository;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.dto.ReservationDetailResponse;
import com.cmc.classhub.reservation.dto.ReservationRequest;
import com.cmc.classhub.reservation.dto.ReservationResponse;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private final OnedayClassRepository onedayClassRepository;

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

        if (reservationRepository.existsBySessionIdAndMemberId(request.getSessionId(), member.getId())) {
            throw new IllegalStateException("이미 해당 일정에 예약하셨습니다.");
        }

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

    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservationDetails(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        Member member = memberRepository.findById(reservation.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        OnedayClass onedayClass = onedayClassRepository.findBySessionsId(reservation.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("클래스 정보를 찾을 수 없습니다."));

        Session session = onedayClass.getSessions().stream()
                .filter(s -> s.getId().equals(reservation.getSessionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("세션 정보를 찾을 수 없습니다."));

        return ReservationDetailResponse.builder()
                .reservationId(reservation.getId())
                .classTitle(onedayClass.getTitle())
                .classLocation(onedayClass.getLocation())
                .date(session.getDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .applicantName(member.getName())
                .phoneNumber(member.getPhone())
                .capacity(session.getCapacity())
                .currentNum(session.getCurrentNum())
                .sessionStatus(session.getStatus().name())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReservationDetailResponse> searchMyReservations(String name, String phone) {
        // 1. 회원 찾기
        Member member = memberRepository.findByNameAndPhone(name, phone)
                .orElse(null);

        if (member == null) {
            return Collections.emptyList();
        }

        List<Reservation> myReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getMemberId().equals(member.getId()))
                .sorted((a, b) -> b.getId().compareTo(a.getId())) // 최신순 정렬
                .toList();

        // 3. 상세 정보로 변환
        return myReservations.stream().map(reservation -> {
            try {
                OnedayClass onedayClass = onedayClassRepository.findBySessionsId(reservation.getSessionId())
                        .orElseThrow();
                Session session = onedayClass.getSessions().stream()
                        .filter(s -> s.getId().equals(reservation.getSessionId()))
                        .findFirst()
                        .orElseThrow();

                return ReservationDetailResponse.builder()
                        .reservationId(reservation.getId())
                        .classTitle(onedayClass.getTitle())
                        .classLocation(onedayClass.getLocation())
                        .date(session.getDate())
                        .startTime(session.getStartTime())
                        .endTime(session.getEndTime())
                        .applicantName(member.getName())
                        .phoneNumber(member.getPhone())
                        .build();
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));

        OnedayClass onedayClass = onedayClassRepository.findBySessionsId(reservation.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("클래스 정보를 찾을 수 없습니다."));

        Session session = onedayClass.getSessions().stream()
                .filter(s -> s.getId().equals(reservation.getSessionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("세션 정보를 찾을 수 없습니다."));

        session.cancel();

        // 2. 예약 내역 삭제
        reservationRepository.delete(reservation);
    }

}
