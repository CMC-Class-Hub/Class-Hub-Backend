package com.cmc.classhub.message.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTemplateType {
    APPLY_CONFIRMED("예약 완료 안내", "수업 예약 직후 자동으로 발송됩니다", "KA01TP260209062000182KPua00zv4Mw"),
    REMINDER_D3("D-3 리마인더", "수업 3일 전에 자동으로 발송됩니다", "KA01TP260204090209679pIynOqUgvLS"),
    REMINDER_D1("D-1 리마인더", "수업 1일 전에 자동으로 발송됩니다", "KA01TP260204090415000t4W32EUY88r");

    private final String title;
    private final String description;
    private final String templateId;
}
