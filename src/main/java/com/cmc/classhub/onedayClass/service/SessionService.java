package com.cmc.classhub.onedayClass.service;

import com.cmc.classhub.onedayClass.domain.OnedayClass;
import com.cmc.classhub.onedayClass.domain.SessionStatus;
import com.cmc.classhub.onedayClass.dto.SessionCreateRequest;
import com.cmc.classhub.onedayClass.dto.SessionResponse;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.cmc.classhub.onedayClass.domain.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import com.cmc.classhub.onedayClass.dto.SessionUpdateRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final OnedayClassRepository classRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // 1. 클래스의 모든 세션 조회 (삭제되지 않은 것만)
    public List<SessionResponse> getSessionsByClassId(Long classId) {
        OnedayClass onedayClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클래스입니다."));

        // ✅ 삭제된 클래스의 세션은 조회 불가
        if (onedayClass.isDeleted()) {
            throw new IllegalArgumentException("삭제된 클래스입니다.");
        }

        return onedayClass.getSessions().stream()
                .filter(session -> !session.isDeleted()) // ✅ 삭제되지 않은 세션만
                .map(SessionResponse::from)
                .toList();
    }

    // 2. 특정 세션 조회 (삭제되지 않은 것만)
    public SessionResponse getSessionById(Long sessionId) {
        System.out.println("세션 조회 with = " + sessionId);

        OnedayClass onedayClass = classRepository.findBySessionsId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션이 속한 클래스를 찾을 수 없습니다."));

        // ✅ 삭제된 클래스의 세션은 조회 불가
        if (onedayClass.isDeleted()) {
            throw new IllegalArgumentException("삭제된 클래스의 세션입니다.");
        }

        System.out.println("세션 조회 성공 with = " + sessionId);

        Session session = onedayClass.getSessions().stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세션입니다."));

        // ✅ 삭제된 세션은 조회 불가
        if (session.isDeleted()) {
            throw new IllegalArgumentException("삭제된 세션입니다.");
        }

        return SessionResponse.from(session);
    }

    // 3. 세션 생성
    @Transactional
    public Long createSession(SessionCreateRequest request) {
        OnedayClass onedayClass = classRepository.findById(request.templateId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클래스입니다."));

        // ✅ 삭제된 클래스에는 세션 추가 불가
        if (onedayClass.isDeleted()) {
            throw new IllegalArgumentException("삭제된 클래스에는 세션을 추가할 수 없습니다.");
        }

        Session session = Session.builder()
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .price(request.price())
                .capacity(request.capacity())
                .build();

        onedayClass.addSession(session);
        entityManager.flush();
        return session.getId();
    }

    // 4. 세션 수정
    @Transactional
    public void updateSession(Long sessionId, SessionUpdateRequest request) {
        OnedayClass onedayClass = classRepository.findBySessionsId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션이 속한 클래스를 찾을 수 없습니다."));

        // ✅ 삭제된 클래스의 세션은 수정 불가
        if (onedayClass.isDeleted()) {
            throw new IllegalArgumentException("삭제된 클래스의 세션은 수정할 수 없습니다.");
        }

        Session session = onedayClass.getSessions().stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세션입니다."));

        // ✅ 삭제된 세션은 수정 불가
        if (session.isDeleted()) {
            throw new IllegalArgumentException("삭제된 세션은 수정할 수 없습니다.");
        }

        session.update(
                request.date(),
                request.startTime(),
                request.endTime(),
                request.price(),
                request.capacity());
    }

    // 5. 세션 상태 변경
    @Transactional
    public void updateSessionStatus(Long sessionId, String status) {
        OnedayClass onedayClass = classRepository.findBySessionsId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션이 속한 클래스를 찾을 수 없습니다."));

        // ✅ 삭제된 클래스의 세션은 상태 변경 불가
        if (onedayClass.isDeleted()) {
            throw new IllegalArgumentException("삭제된 클래스의 세션입니다.");
        }

        Session session = onedayClass.getSessions().stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세션입니다."));

        // ✅ 삭제된 세션은 상태 변경 불가
        if (session.isDeleted()) {
            throw new IllegalArgumentException("삭제된 세션은 상태를 변경할 수 없습니다.");
        }

        session.updateStatus(SessionStatus.valueOf(status));
    }

    // 6. 세션 삭제 (Soft Delete)
    @Transactional
    public void deleteSession(Long sessionId) {
        OnedayClass onedayClass = classRepository.findBySessionsId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션이 속한 클래스를 찾을 수 없습니다."));

        Session session = onedayClass.getSessions().stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세션입니다."));

        // ✅ Soft Delete: isDeleted를 true로 설정
        session.delete();

        // DB에서 실제로 삭제하지 않음
        // onedayClass.getSessions().removeIf(...) ← 이건 하드 삭제이므로 사용 안 함
    }

    // 7. 세션 복원 (선택사항)
    @Transactional
    public void restoreSession(Long sessionId) {
        OnedayClass onedayClass = classRepository.findBySessionsId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션이 속한 클래스를 찾을 수 없습니다."));

        Session session = onedayClass.getSessions().stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세션입니다."));

        // ✅ 삭제된 세션 복원
        session.restore();
    }
}