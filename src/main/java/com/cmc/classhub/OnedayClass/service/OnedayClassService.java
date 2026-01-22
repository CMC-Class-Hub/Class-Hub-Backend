package com.cmc.classhub.OnedayClass.service;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
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

    public OnedayClassDetailResponse getSharedClassDetail(String shareCode) {
        OnedayClass onedayClass = onedayClassRepository.findByShareCodeWithSessions(shareCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 클래스 링크입니다."));

        return OnedayClassDetailResponse.from(onedayClass);
    }

    public Optional<OnedayClass> findById(Long onedayClassId) {
        return onedayClassRepository.findById(onedayClassId);
    }
}
