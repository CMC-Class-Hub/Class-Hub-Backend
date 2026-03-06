-- ==============================================
-- V3: reservations.status에 PENDING 추가
-- ==============================================

-- MySQL ENUM 타입 수정 (PENDING 추가)
ALTER TABLE reservations
MODIFY COLUMN status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') NOT NULL;
