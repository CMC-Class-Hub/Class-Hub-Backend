package com.cmc.classhub.reservation.service;

import com.cmc.classhub.onedayClass.domain.OnedayClass;
import com.cmc.classhub.onedayClass.domain.Session;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.reservation.domain.Member;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.domain.ReservationStatus;
import com.cmc.classhub.reservation.dto.ReservationCreateResponse;
import com.cmc.classhub.reservation.dto.ReservationDetailResponse;
import com.cmc.classhub.reservation.dto.ReservationRequest;
import com.cmc.classhub.reservation.dto.ReservationResponse;
import com.cmc.classhub.reservation.repository.MemberRepository;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import com.cmc.classhub.payment.repository.PaymentRepository;
import com.cmc.classhub.payment.domain.PaymentStatus;
import com.cmc.classhub.onedayClass.repository.SessionRepository;
import org.springframework.scheduling.annotation.Scheduled;

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
        private final PaymentRepository paymentRepository;
        private final SessionRepository sessionRepository;

        public ReservationCreateResponse createReservation(ReservationRequest request, Long onedayClassId) {
                // 1. 회원 조회 또는 생성 (게스트)
                Member member = memberRepository
                                .findByNameAndPhone(request.getApplicantName(), request.getPhoneNumber())
                                .orElseGet(() -> createGuestMember(request));

                // 3. 세션 조회 (비관적 락 적용)
                Session session = sessionRepository.findByIdWithLock(request.getSessionId())
                                .orElseThrow(() -> new IllegalArgumentException("세션 정보를 찾을 수 없습니다."));

                // 4. 중복 예약 확인 (확정된 예약이 있는 경우에만 차단)
                if (reservationRepository.existsBySessionIdAndMemberAndStatus(session.getId(), member,
                                ReservationStatus.CONFIRMED)) {
                        throw new IllegalStateException("이미 확정된 예약이 있는 일정입니다.");
                }

                // 5. 세션 예약 처리 (정원 체크 포함)
                session.join();

                // 6. 예약 생성 (상태: PENDING)
                Reservation reservation = Reservation.apply(session.getId(), member);

                Reservation savedReservation = reservationRepository.save(reservation);
                return ReservationCreateResponse.builder()
                                .reservationCode(savedReservation.getReservationCode())
                                .build();
        }

        /**
         * 예약 확정 처리 (결제 성공 시 호출)
         */
        public void completeReservation(String reservationCode) {
                Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

                // 1. 상태를 CONFIRMED로 변경
                reservation.confirm();

                // 2. 동일한 세션에 대해 해당 사용자의 다른 PENDING 예약이 있다면 모두 취소 처리 (인원 원복 포함)
                List<Reservation> duplicates = reservationRepository.findBySessionIdAndMemberAndStatus(
                                reservation.getSessionId(), reservation.getMember(), ReservationStatus.PENDING);

                for (Reservation duplicate : duplicates) {
                        if (!duplicate.getReservationCode().equals(reservationCode)) {
                                failReservation(duplicate.getReservationCode());
                        }
                }

                // 3. 예약 확정 알림톡 발송
                try {
                        messageService.send(com.cmc.classhub.message.domain.MessageTemplateType.APPLY_CONFIRMED,
                                        reservation.getId());
                } catch (Exception e) {
                        // 알림톡 발송 실패가 예약 프로세스를 방해하면 안 됨
                        e.printStackTrace();
                }
        }

        /**
         * 예약 실패 처리 (결제 실패 시 호출)
         */
        public void failReservation(String reservationCode) {
                Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

                // 이미 취소된 경우 무시
                if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                        return;
                }

                // 1. 상태를 CANCELLED로 변경
                reservation.cancel();

                // 2. 세션 인원 원복 (비관적 락 적용)
                Session session = sessionRepository.findByIdWithLock(reservation.getSessionId())
                                .orElseThrow(() -> new IllegalArgumentException("세션 정보를 찾을 수 없습니다."));

                session.cancel();
        }

        /**
         * 15분 동안 결제가 완료되지 않은 PENDING 예약을 자동으로 취소 처리
         */
        @Scheduled(fixedRate = 60000) // 1분마다 실행
        @Transactional
        public void expirePendingReservations() {
                LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(15);
                List<Reservation> expiredReservations = reservationRepository.findByStatusAndCreatedAtBefore(
                                ReservationStatus.PENDING, expiryTime);

                for (Reservation reservation : expiredReservations) {
                        // 1. 예약 취소 및 인원 원복 처리
                        failReservation(reservation.getReservationCode());

                        // 2. 연관된 결제 정보가 있다면 실패 처리
                        paymentRepository.findByReservationId(reservation.getId())
                                        .ifPresent(payment -> {
                                                if (payment.getStatus() == PaymentStatus.PENDING) {
                                                        payment.fail("TIMEOUT", "결제 시간 초과(15분)");
                                                }
                                        });
                }
        }

        @Transactional(readOnly = true)
        public List<ReservationResponse> getReservationsBySession(Long sessionId) {
                // 1. 해당 세션의 모든 예약 조회
                List<Reservation> reservations = reservationRepository.findAllBySessionId(sessionId);

                // 2. 예약 정보 + 회원 정보 매핑 후, 이름+전화번호 기준 최신 예약만 필터링
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
                                .sorted((r1, r2) -> r2.getAppliedAt().compareTo(r1.getAppliedAt())) // 최신순 정렬
                                .collect(Collectors.toMap(
                                                r -> r.getApplicantName() + r.getPhoneNumber(), // 중복체크 키
                                                r -> r, // 값
                                                (existing, replacement) -> existing // 이미 존재하면 기존값(최신) 유지
                                ))
                                .values().stream()
                                .sorted((r1, r2) -> r2.getAppliedAt().compareTo(r1.getAppliedAt())) // 최종 결과 정렬 유지
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
