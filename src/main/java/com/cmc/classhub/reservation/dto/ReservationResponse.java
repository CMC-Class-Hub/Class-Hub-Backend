package com.cmc.classhub.reservation.dto;

import java.time.LocalDateTime;

import com.cmc.classhub.reservation.domain.ReservationStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponse {
    private Long reservationId;
    private Long studentId;
    private String applicantName;
    private String phoneNumber;
    private LocalDateTime appliedAt; 
    private String reservationStatus;
}