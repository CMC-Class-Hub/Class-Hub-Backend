package com.cmc.classhub.settlement.service;

import com.cmc.classhub.settlement.domain.Settlement;
import com.cmc.classhub.settlement.domain.SettlementStatus;
import com.cmc.classhub.settlement.dto.SettlementResponse;
import com.cmc.classhub.settlement.dto.SettlementSummaryResponse;
import com.cmc.classhub.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

  private final SettlementRepository settlementRepository;

  @Transactional
  public void createSettlement(Long instructorId, Long reservationId, Integer amount) {
    Settlement settlement = Settlement.create(instructorId, reservationId, amount);
    Objects.requireNonNull(settlementRepository.save(settlement));
  }

  @Transactional
  public SettlementResponse paySettlement(Long settlementId) {
    Settlement settlement = settlementRepository.findById(settlementId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정산 내역입니다."));
    settlement.pay();
    return SettlementResponse.from(settlement);
  }

  @Transactional
  public void cancelSettlementByReservationId(Long reservationId) {
    settlementRepository.findByReservationId(reservationId)
        .ifPresent(Settlement::cancel);
  }

  public SettlementSummaryResponse getSettlementsBySession(Long sessionId) {
    List<Settlement> settlements = settlementRepository.findAllBySessionId(sessionId);
    return createSummary(settlements);
  }

  public SettlementSummaryResponse getSettlementsByInstructor(Long instructorId) {
    List<Settlement> settlements = settlementRepository.findAllByInstructorId(instructorId);
    return createSummary(settlements);
  }

  private SettlementSummaryResponse createSummary(List<Settlement> settlements) {
    List<SettlementResponse> responses = settlements.stream()
        .map(SettlementResponse::from)
        .collect(Collectors.toList());

    long totalPaidAmount = settlements.stream()
        .filter(s -> s.getStatus() == SettlementStatus.PAID)
        .mapToLong(Settlement::getAmount)
        .sum();

    long totalReadyAmount = settlements.stream()
        .filter(s -> s.getStatus() == SettlementStatus.READY)
        .mapToLong(Settlement::getAmount)
        .sum();

    return SettlementSummaryResponse.of(responses, totalPaidAmount, totalReadyAmount);
  }
}
