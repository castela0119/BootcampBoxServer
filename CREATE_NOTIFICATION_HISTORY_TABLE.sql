-- 좋아요 알림 발송 이력 테이블 생성
CREATE TABLE IF NOT EXISTS notification_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL COMMENT '알림 수신자 ID',
    sender_id BIGINT NOT NULL COMMENT '알림 발신자 ID',
    notification_type VARCHAR(20) NOT NULL COMMENT '알림 타입 (like, comment)',
    target_type VARCHAR(20) NOT NULL COMMENT '대상 타입 (post, comment)',
    target_id BIGINT NOT NULL COMMENT '대상 ID',
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '발송 시간',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 복합 인덱스 (중복 방지용)
    UNIQUE KEY uk_notification_history (recipient_id, sender_id, notification_type, target_type, target_id),
    
    -- 외래키 제약조건
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- 인덱스
    INDEX idx_recipient_sender (recipient_id, sender_id),
    INDEX idx_target (target_type, target_id),
    INDEX idx_sent_at (sent_at),
    INDEX idx_notification_type (notification_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='알림 발송 이력 테이블';

-- 기존 데이터 정리를 위한 임시 테이블 (필요시)
-- CREATE TABLE IF NOT EXISTS temp_notification_history AS
-- SELECT DISTINCT 
--     n.user_id as recipient_id,
--     n.sender_id,
--     n.type as notification_type,
--     n.target_type,
--     n.target_id,
--     n.created_at as sent_at
-- FROM notifications n 
-- WHERE n.type = 'like' AND n.sender_id IS NOT NULL; 