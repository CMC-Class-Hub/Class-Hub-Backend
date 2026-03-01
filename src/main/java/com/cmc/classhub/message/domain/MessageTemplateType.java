package com.cmc.classhub.message.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTemplateType {
    // 자동 발송
    AUTO_APPLY_CONFIRMED("예약 완료 안내", "수업 예약 직후 자동으로 발송됩니다", "KA01TP260215070442808zhb7CLb0noi"),
    AUTO_REMINDER_D3("D-3 리마인더", "수업 3일 전에 자동으로 발송됩니다", "KA01TP2602150709395324i0zKj315nh"),
    AUTO_REMINDER_D1("D-1 리마인더", "수업 1일 전에 자동으로 발송됩니다", "KA01TP260215070757673tIwL1cxuxxc"),

    // 수동 발송 (강사용)
    MANUAL_LOC_CHG("장소 변경 안내", "수업 장소가 변경되었을 때 발송합니다", "KA01TP260301045758419wVCdFCAYP7R"),
    MANUAL_TIME_CHG("시간 변경 안내", "수업 시간이 변경되었을 때 발송합니다", "KA01TP260301045831556SQZbFq7AMie"),
    MANUAL_DELAY("시작 지연 안내", "수업이 지연될 때 발송합니다", "KA01TP260301045721989SaR2NbBRgMw"),
    MANUAL_CANCEL("수업 취소 안내", "수업이 취소되었을 때 발송합니다", "KA01TP260301043919969sgsA9bcaJlt");

    private final String title;
    private final String description;
    private final String templateId;
}
