package com.cmc.classhub.onedayClass.service;

import com.cmc.classhub.onedayClass.domain.OnedayClass;
import com.cmc.classhub.onedayClass.domain.Session;
import com.cmc.classhub.onedayClass.dto.LinkShareStatusUpdateRequest;
import com.cmc.classhub.onedayClass.dto.OnedayClassCreateRequest;
import com.cmc.classhub.onedayClass.dto.OnedayClassResponse;
import com.cmc.classhub.onedayClass.repository.OnedayClassRepository;
import com.cmc.classhub.reservation.domain.Reservation;
import com.cmc.classhub.reservation.domain.ReservationStatus;
import com.cmc.classhub.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OnedayClassService {

    private final OnedayClassRepository onedayClassRepository;
    private final ReservationRepository reservationRepository;
    // 1. 강사의 모든 클래스 조회 (삭제되지 않은 것만)
    public List<OnedayClassResponse> getClassesByInstructor(Long instructorId) {
        List<OnedayClass> classes = onedayClassRepository
                .findAllByInstructorId(instructorId);

        return classes.stream()
                .filter(onedayClass -> !onedayClass.isDeleted()) // ✅ 삭제되지 않은 것만
                .map(OnedayClassResponse::from)
                .toList();
    }

    // 2. 특정 클래스 조회 (삭제되지 않은 것만)
    public OnedayClassResponse getClassById(Long classId) {
        OnedayClass onedayClass = onedayClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클래스입니다."));

        // ✅ 삭제된 클래스는 조회 불가
        if (onedayClass.isDeleted()) {
            throw new IllegalArgumentException("삭제된 클래스입니다.");
        }

        return OnedayClassResponse.from(onedayClass);
    }

    // 3. 클래스 생성 (세션 없이)
    @Transactional
    public Long createOnedayClass(OnedayClassCreateRequest request, Long instructorId) {
        OnedayClass onedayClass = OnedayClass.builder()
                .instructorId(instructorId)
                .title(request.name())
                .description(request.description())
                .location(request.location())
                .locationDescription(request.locationDetails())
                .material(request.preparation())
                .parkingInfo(request.parkingInfo())
                .guidelines(request.instructions())
                .policy(request.cancellationPolicy())
                .build();

        // ✅ 이미지 설정
        onedayClass.updateImages(request.images());

        return onedayClassRepository.save(onedayClass).getId();
    }

    // 4. 클래스 수정
    @Transactional
    public void updateOnedayClass(Long classId, OnedayClassCreateRequest request) {
        OnedayClass onedayClass = onedayClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클래스입니다."));

        // ✅ 삭제된 클래스는 수정 불가
        if (onedayClass.isDeleted()) {
            throw new IllegalArgumentException("삭제된 클래스는 수정할 수 없습니다.");
        }

        onedayClass.update(
                request.name(),
                request.description(),
                request.location(),
                request.locationDetails(),
                request.preparation(),
                request.parkingInfo(),
                request.instructions(),
                request.cancellationPolicy(),
                request.images());
    }

    // 5. 클래스 삭제 (Soft Delete)
    @Transactional
    public void deleteClass(Long classId) {
        OnedayClass onedayClass = onedayClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클래스입니다."));

        List<Long> sessionIds = onedayClass.getSessions().stream()
                .map(Session::getId)
                .toList();

        // ✅ 각 세션의 예약 상태 확인
        List<Reservation> allReservations = reservationRepository.findAllBySessionIdIn(sessionIds);
        
        // 예약이 하나도 없는 경우 → Hard Delete (DB에서 완전 삭제)
        if (allReservations.isEmpty()) {
            onedayClassRepository.delete(onedayClass);
            return;
        }

        // 취소되지 않은 예약이 있는지 확인
        boolean hasActiveReservation = allReservations.stream()
                .anyMatch(r -> r.getStatus() != ReservationStatus.CANCELLED);

        if (hasActiveReservation) {
            throw new IllegalStateException("취소되지 않은 예약이 존재하여 클래스를 삭제할 수 없습니다.");
        }


        onedayClass.delete();
        onedayClass.getSessions().forEach(Session::delete);

    }

    // 6. 클래스 복원 (선택사항)
    @Transactional
    public void restoreClass(Long classId) {
        OnedayClass onedayClass = onedayClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클래스입니다."));

        // ✅ 삭제된 클래스 복원
        onedayClass.restore();

        // ✅ 연관된 세션도 모두 복원
        onedayClass.getSessions().forEach(Session::restore);
    }

    // 7. 클래스 코드로 클래스 조회 (삭제되지 않은 것만)
    public OnedayClassResponse getClassByCode(String classCode) {
        OnedayClass onedayClass = onedayClassRepository.findByClassCode(classCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클래스 코드입니다."));

        if (onedayClass.isDeleted()) {
            throw new IllegalArgumentException("삭제된 클래스입니다.");
        }

        return OnedayClassResponse.from(onedayClass);
    }

    @Transactional
    public void updateLinkShareStatus(
        Long classId,
        LinkShareStatusUpdateRequest status
    ) {
    OnedayClass onedayClass = onedayClassRepository.findById(classId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클래스입니다."));

    if (onedayClass.isDeleted()) {
        throw new IllegalStateException("삭제된 클래스는 링크 공유 상태를 변경할 수 없습니다.");
    }

        onedayClass.updateLinkShareStatus(status.linkShareStatus());
    }
}