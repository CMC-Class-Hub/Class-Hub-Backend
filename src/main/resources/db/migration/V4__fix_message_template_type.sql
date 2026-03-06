-- ==============================================
-- V4: messages.template_type ENUM 업데이트
-- ==============================================

-- 1. 기존 데이터 이름 변경 (ENUM 수정 전에 VARCHAR로 임시 변환)
ALTER TABLE messages MODIFY COLUMN template_type VARCHAR(50) NOT NULL;

-- 2. 기존 데이터 마이그레이션
UPDATE messages SET template_type = 'AUTO_APPLY_CONFIRMED' WHERE template_type = 'APPLY_CONFIRMED';
UPDATE messages SET template_type = 'AUTO_REMINDER_D3' WHERE template_type = 'REMINDER_D3';
UPDATE messages SET template_type = 'AUTO_REMINDER_D1' WHERE template_type = 'REMINDER_D1';

-- 3. 새 ENUM으로 변경 (모든 타입 포함)
ALTER TABLE messages MODIFY COLUMN template_type ENUM(
    'AUTO_APPLY_CONFIRMED',
    'AUTO_REMINDER_D3',
    'AUTO_REMINDER_D1',
    'MANUAL_LOC_CHG',
    'MANUAL_TIME_CHG',
    'MANUAL_DELAY',
    'MANUAL_CANCEL'
) NOT NULL;
