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

import java.time.LocalDateTime;
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

        public String createReservation(ReservationRequest request, Long onedayClassId) {
                // 1. 회원 조회 또는 생성 (게스트)
                Member member = memberRepository
                                .findByNameAndPhone(request.getApplicantName(), request.getPhoneNumber())
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

                Reservation savedReservation = reservationRepository.save(reservation);
                String reservationCode = savedReservation.getReservationCode();

                // 7. 예약 확정 알림톡 발송
                try {
                        messageService.send(com.cmc.classhub.message.domain.MessageTemplateType.APPLY_CONFIRMED,
                                        savedReservation.getId());
                } catch (Exception e) {
                        // 알림톡 발송 실패가 예약 프로세스를 방해하면 안 됨
                        e.printStackTrace();
                }

                return reservationCode;
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
                                                        .sentD3Notification(reservation.isSentD3Notification())
                                                        .sentD1Notification(reservation.isSentD1Notification())
                                                        .build();
                                })
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public ReservationDetailResponse getReservationDetails(String reservationCode) {
                Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
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
                                .classCode(onedayClass.getClassCode())
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
        public List<ReservationDetailResponse> searchMyReservations(String name, String phone) {
                // 1. 회원 찾기
                Member member = memberRepository.findByNameAndPhone(name, phone)
                                .orElse(null);

                // 2. 회원이 없으면 빈 리스트 반환
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

        @Transactional(readOnly = true)
        public List<ReservationDetailResponse> searchReservationsByClassCode(String classCode) {
                // 1. 클래스 찾기
                OnedayClass onedayClass = onedayClassRepository.findByClassCode(classCode)
                                .orElse(null);

                // 2. 클래스가 없으면 빈 리스트 반환
                if (onedayClass == null) {
                        return Collections.emptyList();
                }

                // 3. 해당 클래스의 모든 세션 ID 수집
                List<Long> sessionIds = onedayClass.getSessions().stream()
                                .map(Session::getId)
                                .collect(Collectors.toList());

                // 4. 모든 세션의 예약 목록 조회
                List<Reservation> reservations = reservationRepository.findAll().stream()
                                .filter(r -> sessionIds.contains(r.getSessionId()))
                                .sorted((a, b) -> b.getId().compareTo(a.getId())) // 최신순 정렬
                                .toList();

                // 5. 상세 정보로 변환
                return reservations.stream().map(reservation -> {
                        try {
                                Member member = memberRepository.findById(reservation.getMember().getId())
                                                .orElseThrow();
                                Session session = onedayClass.getSessions().stream()
                                                .filter(s -> s.getId().equals(reservation.getSessionId()))
                                                .findFirst()
                                                .orElseThrow();

                                return ReservationDetailResponse.builder()
                                                .reservationId(reservation.getId())
                                                .classTitle(onedayClass.getTitle())
                                                .classLocation(onedayClass.getLocation())
                                                .classCode(onedayClass.getClassCode())
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
                        } catch (Exception e) {
                                return null;
                        }
                }).filter(Objects::nonNull).collect(Collectors.toList());
        }

        public void cancelReservation(String reservationCode) {
                Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                                .orElseThrow();

                // 1. 세션 조회
                OnedayClass onedayClass = onedayClassRepository.findBySessionsId(reservation.getSessionId())
                                .orElseThrow(() -> new IllegalArgumentException("세션이 속한 클래스를 찾을 수 없습니다."));
                Session session = onedayClass.getSessions().stream()
                                .filter(s -> s.getId().equals(reservation.getSessionId()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

                // 2. 세션 시작 12시간 전까지만 취소 가능
                LocalDateTime sessionStart = LocalDateTime.of(session.getDate(), session.getStartTime());
                LocalDateTime deadline = sessionStart.minusHours(12);
                if (LocalDateTime.now().isAfter(deadline)) {
                        throw new IllegalStateException("세션 시작 12시간 전까지만 취소 가능합니다.");
                }

                // 3. 예약 상태 변경 (CANCELLED)
                reservation.cancel();

                // 4. 세션 인원 감소
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
