package com.cmc.classhub.reservation.controller;

import com.cmc.classhub.onedayClass.dto.OnedayClassResponse;
import com.cmc.classhub.onedayClass.dto.SessionResponse;
import com.cmc.classhub.onedayClass.service.OnedayClassService;
import com.cmc.classhub.onedayClass.service.SessionService;
import com.cmc.classhub.reservation.dto.ReservationDetailResponse;
import com.cmc.classhub.reservation.dto.ReservationRequest;
import com.cmc.classhub.reservation.dto.ReservationResponse;
import com.cmc.classhub.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservation", description = "예약 관리 API")
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final OnedayClassService onedayClassService;
    private final SessionService sessionService;

    @Operation(summary = "예약 생성", description = "원데이클래스 예약을 생성합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<String> reserve(
            @Parameter(description = "클래스 ID") @RequestParam Long onedayClassId,
            @RequestBody @Valid ReservationRequest request) {
        String reservationCode = reservationService.createReservation(request, onedayClassId);
        return ResponseEntity.ok(reservationCode);
    }

    @Operation(summary = "세션별 예약 목록 조회", description = "특정 세션의 예약 목록을 조회합니다")
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId) {
        List<ReservationResponse> responses = reservationService.getReservationsBySession(sessionId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 상세 조회", description = "예약 코드로 상세 정보를 조회합니다")
    @GetMapping("/{reservationCode}")
    public ResponseEntity<ReservationDetailResponse> getReservationDetail(
            @Parameter(description = "예약 코드") @PathVariable String reservationCode) {
        ReservationDetailResponse response = reservationService.getReservationDetails(reservationCode);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 예약 검색", description = "이름과 전화번호로 본인의 예약을 검색합니다")
    @GetMapping("/search")
    public ResponseEntity<List<ReservationDetailResponse>> searchReservations(
            @Parameter(description = "예약자 이름") @RequestParam String name,
            @Parameter(description = "전화번호") @RequestParam String phone) {
        List<ReservationDetailResponse> results = reservationService.searchMyReservations(name, phone);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "예약 취소", description = "예약 코드로 예약을 취소합니다")
    @DeleteMapping("/{reservationCode}")
    public ResponseEntity<Void> cancelReservation(
            @Parameter(description = "예약 코드") @PathVariable String reservationCode) {
        reservationService.cancelReservation(reservationCode);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "클래스 코드로 클래스 정보 조회", description = "클래스 코드로 클래스 정보를 조회합니다 (공개용)")
    @GetMapping("/code/{classCode}")
    public ResponseEntity<OnedayClassResponse> getClassByCode(
            @Parameter(description = "클래스 코드") @PathVariable String classCode) {
        return ResponseEntity.ok(onedayClassService.getClassByCode(classCode));
    }

    @Operation(summary = "클래스 코드로 예약 조회", description = "클래스 코드로 해당 클래스의 모든 예약을 조회합니다")
    @GetMapping("/code/{classCode}/reservations")
    public ResponseEntity<List<ReservationDetailResponse>> getReservationsByClassCode(
            @Parameter(description = "클래스 코드") @PathVariable String classCode) {
        List<ReservationDetailResponse> results = reservationService.searchReservationsByClassCode(classCode);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "클래스의 세션 목록 조회", description = "클래스 ID로 세션 목록을 조회합니다 (공개용, 지난 날짜 제외)")
    @GetMapping("/{classId}/sessions")
    public ResponseEntity<List<SessionResponse>> getClassSessions(
            @Parameter(description = "클래스 ID") @PathVariable Long classId) {
        return ResponseEntity.ok(
                sessionService.getUpcomingSessionsByClassId(classId));
    }

}
