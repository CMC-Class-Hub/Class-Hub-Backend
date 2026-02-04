package com.cmc.classhub.message.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTemplateType {
    APPLY_CONFIRMED("예약 완료 안내", "KA01TP260204085818135zn5xfzZaaNQ"),
    REMINDER_D3("D-3 리마인더", "KA01TP260204090209679pIynOqUgvLS"),
    REMINDER_D1("D-1 리마인더", "KA01TP260204090415000t4W32EUY88r");

    private final String description;
    private final String templateId;
}
