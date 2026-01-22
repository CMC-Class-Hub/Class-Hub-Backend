package com.cmc.classhub.OnedayClass.service;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.domain.Session;
import com.cmc.classhub.OnedayClass.dto.OnedayClassCreateRequest;
import com.cmc.classhub.OnedayClass.dto.OnedayClassDetailResponse;
import com.cmc.classhub.OnedayClass.repository.OnedayClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OnedayClassService {

    private final OnedayClassRepository onedayClassRepository;

    @Transactional
    public Long createOnedayClass(OnedayClassCreateRequest request, Long instructorId) {
        // 1. 엔티티 생성 (엔티티 생성자에서 shareCode가 자동 생성됨)
        OnedayClass onedayClass = OnedayClass.builder()
                .instructorId(instructorId)
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .price(request.price())
                .deposit(request.deposit())
                .material(request.material())
                .policy(request.policy())
                .build();

        // 2. 세션 추가
        request.sessions().forEach(s -> {
            Session session = Session.builder()
                    .date(s.date())
                    .startTime(s.startTime())
                    .endTime(s.endTime())
                    .capacity(s.capacity())
                    .build();
            onedayClass.addSession(session);
        });

        // 3. 저장 및 ID 반환
        return onedayClassRepository.save(onedayClass).getId();
    }

    public OnedayClassDetailResponse getSharedClassDetail(String shareCode) {
        OnedayClass onedayClass = onedayClassRepository.findByShareCodeWithSessions(shareCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 클래스 링크입니다."));

        return OnedayClassDetailResponse.from(onedayClass);
    }

    public Optional<OnedayClass> findById(Long onedayClassId) {
        return onedayClassRepository.findById(onedayClassId);
    }
}
