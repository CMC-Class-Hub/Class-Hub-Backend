-- ==============================================
-- V2: MVP Extension
-- ==============================================

-- 1. instructors: 프로필 이미지 추가
ALTER TABLE instructors ADD COLUMN profile_url VARCHAR(255);

-- 2. reservations: 출석 상태 추가
ALTER TABLE reservations ADD COLUMN attendance_status VARCHAR(20) NOT NULL DEFAULT 'READY';

-- 3. messages: 수신자 정보 분리 및 컬럼 추가
ALTER TABLE messages ADD COLUMN receiver_name VARCHAR(255);
ALTER TABLE messages ADD COLUMN receiver_phone VARCHAR(255);

-- 기존 receiver 데이터 마이그레이션 (receiver에 전화번호가 저장되어 있었음)
UPDATE messages SET receiver_phone = receiver, receiver_name = '';

-- receiver_name, receiver_phone NOT NULL 제약조건 추가
ALTER TABLE messages MODIFY COLUMN receiver_name VARCHAR(255) NOT NULL;
ALTER TABLE messages MODIFY COLUMN receiver_phone VARCHAR(255) NOT NULL;

-- 기존 receiver 컬럼 삭제
ALTER TABLE messages DROP COLUMN receiver;

-- sender_id, content 컬럼 추가
ALTER TABLE messages ADD COLUMN sender_id BIGINT;
ALTER TABLE messages ADD COLUMN content TEXT;

-- 4. payments 테이블 생성
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    tid VARCHAR(255) UNIQUE,
    order_id VARCHAR(255) NOT NULL,
    amount INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    method VARCHAR(50),
    card_code VARCHAR(50),
    card_name VARCHAR(100),
    card_num VARCHAR(50),
    result_code VARCHAR(50),
    result_msg VARCHAR(255),
    approved_at DATETIME,
    cancelled_at DATETIME,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_payments_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

-- 5. settlements 테이블 생성
CREATE TABLE settlements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instructor_id BIGINT NOT NULL,
    reservation_id BIGINT NOT NULL,
    amount INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'READY',
    paid_at DATETIME,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_settlements_instructor FOREIGN KEY (instructor_id) REFERENCES instructors(id),
    CONSTRAINT fk_settlements_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);
