package com.cmc.classhub.reservation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponse {
    private Long reservationId;
    private String applicantName;
    private String phoneNumber;
}