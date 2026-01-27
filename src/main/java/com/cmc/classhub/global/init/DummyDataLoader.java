package com.cmc.classhub.global.init;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.domain.Session;
import com.cmc.classhub.OnedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.instructor.domain.Instructor;
import com.cmc.classhub.instructor.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final InstructorRepository instructorRepository;
    private final OnedayClassRepository onedayClassRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 이미 데이터가 있다면 실행하지 않음
        if (instructorRepository.count() > 0) {
            return;
        }

        // 1. 강사(Instructor) 생성
        Instructor instructor = Instructor.builder()
                .businessName("취미 공작소")
                .email("adfd@gmail.com")
                .name("김강사")
                .phoneNumber("010-1234-5678")
                .passwordHash("sefsfa")
                .build();
        instructorRepository.save(instructor);
        Long instructorId = instructor.getId();

        // 2. 클래스 데이터 1: 우드카빙
        OnedayClass woodClass = OnedayClass.builder()
                .instructorId(instructorId)
                .title("나만의 우드카빙 도마 만들기")
                .shareCode("TEST01")
                .description("세상에서 하나뿐인 나만의 나무 도마를 직접 깎아보세요. 나무의 결을 느끼며 힐링하는 시간입니다.")
                .location("서울 강남구 테헤란로 123")
                .locationDescription("역삼역 4번 출구에서 도보 5분 거리")
                .price(45000)
                .material("나무 원목, 오일, 샌딩용 사포 (모두 제공)")
                .parkingInfo("건물 내 무료 주차 2시간 지원")
                .guidelines("칼을 사용하는 수업이므로 안전 수칙을 준수해주세요.")
                .policy("수업 3일 전까지 100% 환불 가능")
                .build();

        woodClass.addSession(Session.builder()
                .date(LocalDate.now().plusDays(3))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(17, 0))
                .capacity(6).build());
        woodClass.addSession(Session.builder()
                .date(LocalDate.now().plusDays(5))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(13, 0))
                .capacity(6).build());

        // 3. 클래스 데이터 2: 도자기
        OnedayClass potteryClass = OnedayClass.builder()
                .instructorId(instructorId)
                .title("감성 가득 달항아리 만들기")
                .shareCode("TEST02")
                .description("부드러운 흙의 감촉을 느끼며 자신만의 달항아리를 빚어보세요. 초보자도 쉽게 배울 수 있습니다.")
                .location("서울 성동구 연무장길 45")
                .locationDescription("성수역 근처 카페 거리 내 위치")
                .price(60000)
                .material("백자토, 조각도, 앞치마 (모두 제공)")
                .parkingInfo("주차 공간이 협소하니 가급적 대중교통 이용 부탁드립니다.")
                .guidelines("흙이 묻을 수 있으니 편한 복장으로 오세요.")
                .policy("당일 취소는 환불이 불가합니다.")
                .build();

        potteryClass.addSession(Session.builder()
                .date(LocalDate.now().plusDays(7))
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(16, 0))
                .capacity(4).build());

        // 4. 클래스 데이터 3: 베이킹
        OnedayClass bakingClass = OnedayClass.builder()
                .instructorId(instructorId)
                .title("바삭바삭 휘낭시에 6종 클래스")
                .shareCode("TEST03")
                .description("겉바속촉의 정석! 6가지 다양한 맛의 휘낭시에를 직접 구워보고 포장까지 완벽하게 배워보세요.")
                .location("서울 마포구 어울마당로 88")
                .locationDescription("홍대입구역 9번 출구 인근")
                .price(55000)
                .material("국산 버터, 프랑스산 밀가루, 다양한 토핑")
                .parkingInfo("유료 공영 주차장 이용 가능")
                .guidelines("오븐을 사용하므로 화상에 주의해주세요.")
                .policy("수업 전날 취소 시 50% 환불")
                .build();

        bakingClass.addSession(Session.builder()
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(18, 0))
                .capacity(5).build());

        // 5. 클래스 데이터 4: 요가
        OnedayClass yogaClass = OnedayClass.builder()
                .instructorId(instructorId)
                .title("심신 안정을 위한 하타 요가")
                .shareCode("TEST04")
                .description("지친 일상을 벗어나 호흡에 집중하는 시간을 가져보세요. 몸의 유연성과 마음의 평화를 찾습니다.")
                .location("서울 송파구 석촌호수로 222")
                .locationDescription("석촌호수 동호 인근")
                .price(25000)
                .material("요가 매트 제공, 편한 운동복 지참")
                .parkingInfo("발렛 파킹 가능")
                .guidelines("수업 시작 10분 전까지 도착 부탁드립니다.")
                .policy("수업 1시간 전까지 일정 변경 가능")
                .build();

        yogaClass.addSession(Session.builder()
                .date(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 30))
                .capacity(10).build());

        // 저장
        onedayClassRepository.save(woodClass);
        onedayClassRepository.save(potteryClass);
        onedayClassRepository.save(bakingClass);
        onedayClassRepository.save(yogaClass);
    }
}