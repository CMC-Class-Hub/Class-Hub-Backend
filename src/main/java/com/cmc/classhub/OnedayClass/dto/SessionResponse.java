package com.cmc.classhub.OnedayClass.dto;

import com.cmc.classhub.OnedayClass.domain.Session;

import java.time.LocalDate;
import java.time.LocalTime;

public record SessionResponse(
        Long sessionId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        int currentNum,
        int capacity,
        String status
) {
    public static SessionResponse from(Session session) {
        return new SessionResponse(
                session.getId(),
                session.getDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getEnrollmentCount().getCurrentNum(),
                session.getEnrollmentCount().getCapacity(),
                session.getStatus().name()
        );
    }
}