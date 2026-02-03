package com.cmc.classhub.reservation.service;

import com.cmc.classhub.onedayClass.domain.OnedayClass;
import com.cmc.classhub.onedayClass.domain.Session;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.onedayClass.service.OnedayClassService;
import com.cmc.classhub.reservation.domain.Member;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.dto.ReservationDetailResponse;
import com.cmc.classhub.reservation.dto.ReservationRequest;
import com.cmc.classhub.reservation.dto.ReservationResponse;
import com.cmc.classhub.reservation.repository.MemberRepository;
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
    // 1. 회원 조회 또는 생성 (게스트)
        Member member = memberRepository.findByPhone(request.getPhoneNumber())
                .orElseGet(() -> createGuestMember(request));
        
        // 2. 클래스 조회
        OnedayClass onedayClass = onedayClassRepository.findById(onedayClassId)
                .orElseThrow();
        
        // 3. 세션 조회 (클래스의 세션 목록에서)
        Session session = onedayClass.getSessions().stream()
                .filter(s -> s.getId().equals(request.getSessionId()))
                .findFirst()
                .orElseThrow();
        
        // 4. 중복 예약 확인
        if (reservationRepository.existsBySessionAndMember(session, member)) {
                throw new IllegalStateException("이미 해당 일정에 예약하셨습니다.");
        }
        
        // 5. 세션 예약 처리 (정원 체크 포함)
        session.join();
        
        // 6. 예약 생성 (상태: PENDING)
        Reservation reservation = Reservation.apply(session, member);
        
        return reservationRepository.save(reservation).getId();
}

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsBySession(Long sessionId) {
        // 1. 해당 세션의 모든 예약 조회
        List<Reservation> reservations = reservationRepository.findAllBySessionId(sessionId);

        // 2. 예약 정보 + 회원 정보 매핑하여 반환
        return reservations.stream()
                .map(reservation -> {
                    Member member = memberRepository.findById(reservation.getMember().getId())
                            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

                    return ReservationResponse.builder()
                            .status(reservation.getStatus())
                            .reservationId(reservation.getId())
                            .applicantName(member.getName())
                            .phoneNumber(member.getPhone())
                            .studentId(member.getId())
                            .appliedAt(reservation.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservationDetails(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        Member member = memberRepository.findById(reservation.getMember().getId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        OnedayClass onedayClass = onedayClassRepository.findBySessionsId(reservation.getSession().getId())
                .orElseThrow(() -> new IllegalArgumentException("클래스 정보를 찾을 수 없습니다."));

        Session session = onedayClass.getSessions().stream()
                .filter(s -> s.getId().equals(reservation.getSession().getId()))
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
                .filter(r -> r.getMember().getId().equals(member.getId()))
                .sorted((a, b) -> b.getId().compareTo(a.getId())) // 최신순 정렬
                .toList();

        // 3. 상세 정보로 변환
        return myReservations.stream().map(reservation -> {
            try {
                OnedayClass onedayClass = onedayClassRepository.findBySessionsId(reservation.getSession().getId())
                        .orElseThrow();
                Session session = reservation.getSession();

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
            .orElseThrow();
    
    // 1. 예약 상태 변경 (CANCELLED)
    reservation.cancel();
    
    // 2. 세션 인원 감소
    Session session = reservation.getSession();
    session.cancel();
    
    // Note: 예약 엔티티는 삭제하지 않고 이력 관리
}

    private Member createGuestMember(ReservationRequest request) {
        Member guestMember = Member.builder()
                .name(request.getApplicantName())
                .phone(request.getPhoneNumber())
                .build();
        return memberRepository.save(guestMember);
    }   

}
