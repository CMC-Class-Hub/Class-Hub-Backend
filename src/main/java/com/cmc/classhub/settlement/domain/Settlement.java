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
    private Long sessionId;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    @Builder
    public Settlement(Long instructorId, Long sessionId, Integer amount) {
        this.instructorId = instructorId;
        this.sessionId = sessionId;
        this.amount = amount;
        this.settledAt = LocalDateTime.now();
    }
}
