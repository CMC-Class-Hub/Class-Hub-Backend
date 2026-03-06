package com.cmc.classhub.settlement.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long instructorId;

  @Column(nullable = false)
  private Long reservationId;

  @Column(nullable = false)
  private Integer amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SettlementStatus status;

  private LocalDateTime paidAt;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Builder
  private Settlement(Long instructorId, Long reservationId, Integer amount) {
    this.instructorId = instructorId;
    this.reservationId = reservationId;
    this.amount = amount;
    this.status = SettlementStatus.READY;
    this.createdAt = LocalDateTime.now();
  }

  public static Settlement create(Long instructorId, Long reservationId, Integer amount) {
    return Settlement.builder()
        .instructorId(instructorId)
        .reservationId(reservationId)
        .amount(amount)
        .build();
  }

  public void pay() {
    if (this.status == SettlementStatus.PAID) {
      throw new IllegalStateException("이미 지급 완료된 정산건입니다.");
    }
    this.status = SettlementStatus.PAID;
    this.paidAt = LocalDateTime.now();
  }

  public void cancel() {
    if (this.status == SettlementStatus.PAID) {
      throw new IllegalStateException("이미 지급 완료된 정산건은 취소할 수 없습니다.");
    }
    this.status = SettlementStatus.CANCELLED;
  }
}
