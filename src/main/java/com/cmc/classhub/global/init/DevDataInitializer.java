package com.cmc.classhub.global.init;

import com.cmc.classhub.global.auth.domain.Role;
import com.cmc.classhub.instructor.domain.Instructor;
import com.cmc.classhub.instructor.repository.InstructorRepository;
import com.cmc.classhub.onedayClass.domain.OnedayClass;
import com.cmc.classhub.onedayClass.domain.Session;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.reservation.domain.Member;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.repository.MemberRepository;
import com.cmc.classhub.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private final InstructorRepository instructorRepository;
    private final OnedayClassRepository onedayClassRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 이미 데이터가 있으면 스킵
        if (instructorRepository.count() > 0) {
            log.info("[DEV] 이미 데이터가 존재하여 초기화를 스킵합니다.");
            return;
        }

        log.info("[DEV] 테스트 데이터 초기화 시작");

        // 1. 테스트 강사 생성
        Instructor instructor = createInstructor();

        // 2. 클래스 2개 생성 (각각 세션 포함)
        OnedayClass class1 = createClass1(instructor.getId());
        OnedayClass class2 = createClass2(instructor.getId());

        // 3. 수강생 및 예약 생성
        createReservationsForClass1(class1);
        createReservationsForClass2(class2);

        log.info("[DEV] 테스트 데이터 초기화 완료");
    }

    private Instructor createInstructor() {
        Instructor instructor = Instructor.builder()
                .name("테스트 강사")
                .email("test@classhub.com")
                .phoneNumber("01012345678")
                .passwordHash(passwordEncoder.encode("test1234"))
                .role(Role.USER)
                .build();

        return instructorRepository.save(instructor);
    }

    private OnedayClass createClass1(Long instructorId) {
        OnedayClass onedayClass = OnedayClass.builder()
                .instructorId(instructorId)
                .title("힐링 도자기 클래스")
                .description("초보자도 쉽게 배울 수 있는 도자기 원데이 클래스입니다.")
                .location("서울시 마포구 연남동 123-45")
                .locationDescription("연남동 카페거리 입구, 스타벅스 옆 건물 3층")
                .material("앞치마 (제공됨)")
                .parkingInfo("건물 내 주차 가능 (2시간 무료)")
                .guidelines("수업 10분 전까지 도착해주세요.")
                .policy("수업 3일 전까지 취소 시 전액 환불")
                .build();

        // 세션 추가 (3일 후, 7일 후)
        Session session1 = Session.builder()
                .date(LocalDate.now().plusDays(3))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(16, 0))
                .price(50000)
                .capacity(8)
                .build();

        Session session2 = Session.builder()
                .date(LocalDate.now().plusDays(7))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .price(50000)
                .capacity(8)
                .build();

        onedayClass.addSession(session1);
        onedayClass.addSession(session2);

        return onedayClassRepository.save(onedayClass);
    }

    private OnedayClass createClass2(Long instructorId) {
        OnedayClass onedayClass = OnedayClass.builder()
                .instructorId(instructorId)
                .title("프랑스 가정식 쿠킹 클래스")
                .description("프랑스 현지에서 배운 정통 가정식 레시피를 알려드립니다.")
                .location("서울시 강남구 신사동 567-89")
                .locationDescription("신사역 4번 출구에서 도보 5분")
                .material("없음 (모든 재료 제공)")
                .parkingInfo("인근 공영주차장 이용")
                .guidelines("긴 머리는 묶어주세요. 편한 복장으로 오세요.")
                .policy("수업 7일 전까지 취소 시 전액 환불, 3일 전까지 50% 환불")
                .build();

        // 세션 추가 (1일 후, 5일 후)
        Session session1 = Session.builder()
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(21, 0))
                .price(80000)
                .capacity(6)
                .build();

        Session session2 = Session.builder()
                .date(LocalDate.now().plusDays(5))
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(14, 0))
                .price(80000)
                .capacity(6)
                .build();

        onedayClass.addSession(session1);
        onedayClass.addSession(session2);

        return onedayClassRepository.save(onedayClass);
    }

    private void createReservationsForClass1(OnedayClass onedayClass) {
        Session session = onedayClass.getSessions().get(0);

        // 수강생 3명 생성
        String[][] members = {
                {"김민수", "01011112222"},
                {"이영희", "01022223333"},
                {"박철수", "01033334444"}
        };

        for (String[] memberInfo : members) {
            Member member = memberRepository.save(
                    Member.builder()
                            .name(memberInfo[0])
                            .phone(memberInfo[1])
                            .build()
            );

            Reservation reservation = Reservation.apply(session.getId(), member);
            reservationRepository.save(reservation);
            session.join();
        }
    }

    private void createReservationsForClass2(OnedayClass onedayClass) {
        Session session = onedayClass.getSessions().get(0);

        // 수강생 2명 생성
        String[][] members = {
                {"최지우", "01044445555"},
                {"정하늘", "01055556666"}
        };

        for (String[] memberInfo : members) {
            Member member = memberRepository.save(
                    Member.builder()
                            .name(memberInfo[0])
                            .phone(memberInfo[1])
                            .build()
            );

            Reservation reservation = Reservation.apply(session.getId(), member);
            reservationRepository.save(reservation);
            session.join();
        }
    }
}
