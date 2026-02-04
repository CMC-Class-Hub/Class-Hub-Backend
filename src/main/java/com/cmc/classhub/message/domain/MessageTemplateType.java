package com.cmc.classhub.message.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTemplateType {
    APPLY_CONFIRMED("예약 완료 안내", "KA01_CONFIRM"), // 임시 ID
    REMINDER_D3("D-3 리마인더", "KA02_REMIND_D3"), // 임시 ID
    REMINDER_D1("D-1 리마인더", "KA03_REMIND_D1"); // 임시 ID

    private final String description;
    private final String templateId;
}
