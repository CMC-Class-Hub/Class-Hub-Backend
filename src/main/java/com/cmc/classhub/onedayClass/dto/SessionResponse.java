package com.cmc.classhub.onedayClass.dto;

import com.cmc.classhub.onedayClass.domain.Session;

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
                session.getCurrentNum(),
                session.getCapacity(),
                session.getStatus().name()
        );
    }
}
