-- HOT 게시글 관련 인덱스 생성 스크립트

-- 1. HOT 게시글 조회 성능 향상을 위한 인덱스
CREATE INDEX idx_posts_is_hot_hot_score ON posts(is_hot, hot_score DESC);

-- 2. HOT 점수 업데이트 시간 기준 조회를 위한 인덱스
CREATE INDEX idx_posts_hot_updated_at ON posts(hot_updated_at DESC);

-- 3. HOT 게시글 + 기간 필터링을 위한 복합 인덱스
CREATE INDEX idx_posts_is_hot_created_at ON posts(is_hot, created_at DESC);

-- 4. HOT 점수 + 작성일 기준 정렬을 위한 인덱스
CREATE INDEX idx_posts_hot_score_created_at ON posts(hot_score DESC, created_at DESC);

-- 5. 최근 게시글 조회를 위한 인덱스 (배치 작업용)
CREATE INDEX idx_posts_created_at_desc ON posts(created_at DESC);

-- 6. HOT 게시글 통계 조회를 위한 인덱스
CREATE INDEX idx_posts_is_hot_hot_score_created_at ON posts(is_hot, hot_score, created_at);

-- 인덱스 생성 확인
SHOW INDEX FROM posts WHERE Key_name LIKE '%hot%'; 