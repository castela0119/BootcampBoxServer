-- 알림 테이블 스키마 업데이트
-- 기존 테이블 삭제 후 새로운 스키마로 재생성

-- 기존 테이블 삭제
DROP TABLE IF EXISTS notifications;

-- 새로운 알림 테이블 생성
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,           -- 수신자 ID
    sender_id BIGINT,                  -- 발신자 ID (시스템 알림은 NULL)
    type VARCHAR(50) NOT NULL,         -- 알림 타입 (comment, like, follow, system)
    title VARCHAR(200) NOT NULL,       -- 알림 제목
    content TEXT NOT NULL,             -- 알림 내용
    target_type VARCHAR(50),           -- 대상 타입 (post, comment, user, system)
    target_id BIGINT,                  -- 대상 ID
    is_read BOOLEAN DEFAULT FALSE,     -- 읽음 여부
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 외래키 제약조건
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
    
    -- 인덱스
    INDEX idx_user_created_at (user_id, created_at DESC),
    INDEX idx_user_is_read (user_id, is_read),
    INDEX idx_created_at (created_at)
);

-- 테이블 생성 확인
DESCRIBE notifications; 