package com.cmc.classhub.OnedayClass.service;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.dto.OnedayClassDetailResponse;
import com.cmc.classhub.OnedayClass.repository.OnedayClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OnedayClassReaderService {

    private final OnedayClassRepository onedayClassRepository;

    /**
     * 공유 링크(shareCode)를 통한 클래스 상세 정보 조회
     */
    public OnedayClassDetailResponse getSharedClassDetail(String shareCode) {
        OnedayClass onedayClass = onedayClassRepository.findByShareCode(shareCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 클래스 링크입니다."));

        return OnedayClassDetailResponse.from(onedayClass);
    }
}