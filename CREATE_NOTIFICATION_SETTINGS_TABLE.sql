-- 알림 설정 테이블 생성
CREATE TABLE notification_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,           -- 사용자 ID (1:1 관계)
    comment_enabled BOOLEAN DEFAULT TRUE,     -- 댓글 알림 활성화
    like_enabled BOOLEAN DEFAULT TRUE,        -- 좋아요 알림 활성화
    follow_enabled BOOLEAN DEFAULT TRUE,      -- 팔로우 알림 활성화
    system_enabled BOOLEAN DEFAULT TRUE,      -- 시스템 알림 활성화
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 외래키 제약조건
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- 인덱스
    INDEX idx_user_id (user_id)
);

-- 테이블 생성 확인
DESCRIBE notification_settings;

-- 기존 사용자들을 위한 기본 설정 생성 (선택사항)
-- INSERT INTO notification_settings (user_id, comment_enabled, like_enabled, follow_enabled, system_enabled)
-- SELECT id, TRUE, TRUE, TRUE, TRUE FROM users; 