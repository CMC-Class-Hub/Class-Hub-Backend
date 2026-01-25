package com.cmc.classhub.reservation.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ReservationDetailResponse {
    private Long reservationId;
    private String classTitle;
    private String classLocation;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String applicantName;
    private String phoneNumber;
    private Integer capacity;
    private Integer currentNum;
    private String sessionStatus;
}