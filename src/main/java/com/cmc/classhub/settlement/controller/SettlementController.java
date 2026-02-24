package com.cmc.classhub.settlement.controller;

import com.cmc.classhub.settlement.dto.SettlementResponse;
import com.cmc.classhub.settlement.dto.SettlementSummaryResponse;
import com.cmc.classhub.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

  private final SettlementService settlementService;

  @PatchMapping("/{id}/pay")
  public ResponseEntity<SettlementResponse> paySettlement(@PathVariable Long id) {
    return ResponseEntity.ok(settlementService.paySettlement(id));
  }

  @GetMapping("/sessions/{sessionId}")
  public ResponseEntity<SettlementSummaryResponse> getSettlementsBySession(@PathVariable Long sessionId) {
    return ResponseEntity.ok(settlementService.getSettlementsBySession(sessionId));
  }

  @GetMapping("/instructors/{instructorId}")
  public ResponseEntity<SettlementSummaryResponse> getSettlementsByInstructor(@PathVariable Long instructorId) {
    return ResponseEntity.ok(settlementService.getSettlementsByInstructor(instructorId));
  }
}
