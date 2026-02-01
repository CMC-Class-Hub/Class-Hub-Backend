package com.cmc.classhub.message.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTemplateType {
    APPLY_CONFIRMED("KA01_CONFIRM"), // 임시 ID
    REMINDER_D3("KA02_REMIND_D3"), // 임시 ID
    REMINDER_D1("KA03_REMIND_D1"); // 임시 ID

    private final String templateId;
}
