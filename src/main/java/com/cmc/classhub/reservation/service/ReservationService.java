package com.cmc.classhub.reservation.service;

import com.cmc.classhub.onedayClass.domain.OnedayClass;
import com.cmc.classhub.onedayClass.domain.Session;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
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

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

        private final ReservationRepository reservationRepository;
        private final MemberRepository memberRepository;
        private final OnedayClassRepository onedayClassRepository;
        private final com.cmc.classhub.message.service.MessageService messageService;

        public Long createReservation(ReservationRequest request, Long onedayClassId) {
                // 1. 회원 조회 또는 생성 (게스트)
                Member member = memberRepository.findByNameAndPhone(request.getApplicantName(), request.getPhoneNumber())
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
                if (reservationRepository.existsBySessionIdAndMember(session.getId(), member)) {
                        throw new IllegalStateException("이미 해당 일정에 예약하셨습니다.");
                }

                // 5. 세션 예약 처리 (정원 체크 포함)
                session.join();

                // 6. 예약 생성 (상태: PENDING)
                Reservation reservation = Reservation.apply(session.getId(), member);

                Long reservationId = reservationRepository.save(reservation).getId();

                // 7. 예약 확정 알림톡 발송
                try {
                        messageService.send(com.cmc.classhub.message.domain.MessageTemplateType.APPLY_CONFIRMED,
                                        reservationId);
                } catch (Exception e) {
                        // 알림톡 발송 실패가 예약 프로세스를 방해하면 안 됨
                        e.printStackTrace();
                }

                return reservationId;
        }

        @Transactional(readOnly = true)
        public List<ReservationResponse> getReservationsBySession(Long sessionId) {
                // 1. 해당 세션의 모든 예약 조회
                List<Reservation> reservations = reservationRepository.findAllBySessionId(sessionId);

                // 2. 예약 정보 + 회원 정보 매핑하여 반환
                return reservations.stream()
                                .map(reservation -> {
                                        Member member = memberRepository.findById(reservation.getMember().getId())
                                                        .orElseThrow(() -> new IllegalArgumentException(
                                                                        "회원 정보가 없습니다."));

                                        return ReservationResponse.builder()
                                                        .reservationId(reservation.getId())
                                                        .applicantName(member.getName())
                                                        .phoneNumber(member.getPhone())
                                                        .studentId(member.getId())
                                                        .appliedAt(reservation.getCreatedAt())
                                                        .reservationStatus(reservation.getStatus().name())
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
                                .reservationStatus(reservation.getStatus().name())
                                .build();
        }

        @Transactional(readOnly = true)
        public List<ReservationDetailResponse> searchMyReservations(String name, String phone, String password) {
                // 1. 회원 찾기
                Member member = memberRepository.findByNameAndPhone(name, phone)
                                .orElse(null);

                // 2. 회원 없거나 비밀번호 틀리면 빈 리스트 반환
                if (member == null || !member.getPassword().equals(password)) {
                        return Collections.emptyList();
                }

                List<Reservation> myReservations = reservationRepository.findAll().stream()
                                .filter(r -> r.getMember().getId().equals(member.getId()))
                                .sorted((a, b) -> b.getId().compareTo(a.getId())) // 최신순 정렬
                                .toList();

                // 3. 상세 정보로 변환
                return myReservations.stream().map(reservation -> {
                        try {
                                OnedayClass onedayClass = onedayClassRepository
                                                .findBySessionsId(reservation.getSessionId())
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
                                                .sessionStatus(session.getStatus().name())
                                                .reservationStatus(reservation.getStatus().name())
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
                OnedayClass onedayClass = onedayClassRepository.findBySessionsId(reservation.getSessionId())
                                .orElseThrow(() -> new IllegalArgumentException("세션이 속한 클래스를 찾을 수 없습니다."));
                Session session = onedayClass.getSessions().stream()
                                .filter(s -> s.getId().equals(reservation.getSessionId()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));
                session.cancel();

                // Note: 예약 엔티티는 삭제하지 않고 이력 관리
        }

        private Member createGuestMember(ReservationRequest request) {
                Member guestMember = Member.builder()
                                .name(request.getApplicantName())
                                .password(request.getPassword())
                                .phone(request.getPhoneNumber())
                                .build();
                return memberRepository.save(guestMember);
        }

}
