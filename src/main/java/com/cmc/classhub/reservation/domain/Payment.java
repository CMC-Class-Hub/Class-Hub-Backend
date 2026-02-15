package com.cmc.classhub.reservation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id", nullable = false)
  private Reservation reservation;

  @Column(nullable = true, unique = true)
  private String tid; // 나이스페이 거래 ID

  @Column(nullable = false)
  private String orderId; // 주문 ID (UUID)

  @Column(nullable = false)
  private Integer amount; // 결제 금액

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status; // PENDING, COMPLETED, CANCELLED, FAILED

  private String method; // CARD, VBANK, BANK, CELLPHONE, etc.

  private String cardCode; // 카드사 코드
  private String cardName; // 카드사 이름
  private String cardNum; // 카드 번호 (마스킹)

  private String resultCode; // 결제 결과 코드
  private String resultMsg; // 결제 결과 메시지

  private LocalDateTime approvedAt; // 승인 일시
  private LocalDateTime cancelledAt; // 취소 일시

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Builder
  private Payment(Reservation reservation, String tid, String orderId, Integer amount,
      PaymentStatus status, String method, String cardCode,
      String cardName, String cardNum, String resultCode, String resultMsg) {
    this.reservation = reservation;
    this.tid = tid;
    this.orderId = orderId;
    this.amount = amount;
    this.status = status != null ? status : PaymentStatus.PENDING;
    this.method = method;
    this.cardCode = cardCode;
    this.cardName = cardName;
    this.cardNum = cardNum;
    this.resultCode = resultCode;
    this.resultMsg = resultMsg;
    this.createdAt = LocalDateTime.now();
  }

  // 결제 승인 완료
  public void approve(String tid, String resultCode, String resultMsg) {
    this.tid = tid;
    this.status = PaymentStatus.COMPLETED;
    this.resultCode = resultCode;
    this.resultMsg = resultMsg;
    this.approvedAt = LocalDateTime.now();
  }

  // 결제 실패
  public void fail(String resultCode, String resultMsg) {
    this.status = PaymentStatus.FAILED;
    this.resultCode = resultCode;
    this.resultMsg = resultMsg;
  }

  // 결제 취소
  public void cancel(String resultMsg) {
    if (this.status != PaymentStatus.COMPLETED) {
      throw new IllegalStateException("완료된 결제만 취소할 수 있습니다.");
    }
    this.status = PaymentStatus.CANCELLED;
    this.resultMsg = resultMsg;
    this.cancelledAt = LocalDateTime.now();
  }

  // 결제 정보 업데이트 (카드 정보 등)
  public void updatePaymentInfo(String method, String cardCode, String cardName, String cardNum) {
    this.method = method;
    this.cardCode = cardCode;
    this.cardName = cardName;
    this.cardNum = cardNum;
  }
}
