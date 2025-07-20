-- 게시글 뮤트 테이블 생성
CREATE TABLE post_mutes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,                    -- 뮤트한 사용자 ID
    post_id BIGINT NOT NULL,                    -- 뮤트된 게시글 ID
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 복합 유니크 제약조건 (한 사용자가 같은 게시글을 중복 뮤트할 수 없음)
    UNIQUE KEY unique_user_post (user_id, post_id),
    
    -- 외래키 제약조건
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    
    -- 인덱스
    INDEX idx_user_id (user_id),
    INDEX idx_post_id (post_id),
    INDEX idx_created_at (created_at)
);

-- 테이블 생성 확인
DESCRIBE post_mutes;

-- 인덱스 확인
SHOW INDEX FROM post_mutes; 