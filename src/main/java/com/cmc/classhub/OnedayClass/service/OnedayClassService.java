package com.cmc.classhub.OnedayClass.service;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.domain.Session;
import com.cmc.classhub.OnedayClass.domain.SessionStatus;
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

    @org.springframework.transaction.annotation.Transactional
    public void updateOnedayClass(Long classId, OnedayClassCreateRequest request) {
        OnedayClass onedayClass = onedayClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클래스입니다."));

        onedayClass.update(
                request.title(),
                request.description(),
                request.location(),
                request.locationDescription(),
                request.price(),
                request.material(),
                request.parkingInfo(),
                request.guidelines(),
                request.policy()
        );

        onedayClass.clearSessions();

        for (OnedayClassCreateRequest.SessionCreateRequest sessionReq : request.sessions()) {
            Session session = Session.builder()
                    .date(sessionReq.date())
                    .startTime(sessionReq.startTime())
                    .endTime(sessionReq.endTime())
                    .capacity(sessionReq.capacity())
                    .build();
            onedayClass.addSession(session);
        }
    }

    @Transactional
    public Long createOnedayClass(OnedayClassCreateRequest request, Long instructorId) {
        OnedayClass onedayClass = OnedayClass.builder()
                .instructorId(instructorId)
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .locationDescription(request.locationDescription()) // 추가
                .price(request.price())
                .material(request.material())
                .parkingInfo(request.parkingInfo()) // 추가
                .guidelines(request.guidelines())   // 추가
                .policy(request.policy())
                .build();

        request.sessions().forEach(s -> {
            Session session = Session.builder()
                    .date(s.date())
                    .startTime(s.startTime())
                    .endTime(s.endTime())
                    .capacity(s.capacity())
                    .build();
            onedayClass.addSession(session);
        });

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
