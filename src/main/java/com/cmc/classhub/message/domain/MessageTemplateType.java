package com.cmc.classhub.message.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTemplateType {
    APPLY_CONFIRMED("예약 완료 안내", "수업 예약 직후 자동으로 발송됩니다", "KA01TP260215070442808zhb7CLb0noi"),
    REMINDER_D3("D-3 리마인더", "수업 3일 전에 자동으로 발송됩니다", "KA01TP2602150709395324i0zKj315nh"),
    REMINDER_D1("D-1 리마인더", "수업 1일 전에 자동으로 발송됩니다", "KA01TP260215070757673tIwL1cxuxxc");

    private final String title;
    private final String description;
    private final String templateId;
}
