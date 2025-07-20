-- HOT 관련 필드 null 값 업데이트 스크립트
-- 기존 데이터의 hot_score, is_hot, hot_updated_at이 null인 경우 기본값으로 설정

-- 1. 현재 null 값 개수 확인
SELECT 
    COUNT(*) as total_posts,
    COUNT(CASE WHEN hot_score IS NULL THEN 1 END) as null_hot_score,
    COUNT(CASE WHEN is_hot IS NULL THEN 1 END) as null_is_hot,
    COUNT(CASE WHEN hot_updated_at IS NULL THEN 1 END) as null_hot_updated_at
FROM posts;

-- 2. null 값을 기본값으로 업데이트
UPDATE posts SET hot_score = 0 WHERE hot_score IS NULL;
UPDATE posts SET is_hot = false WHERE is_hot IS NULL;
UPDATE posts SET hot_updated_at = NOW() WHERE hot_updated_at IS NULL;

-- 3. 업데이트 후 확인
SELECT 
    COUNT(*) as total_posts,
    COUNT(CASE WHEN hot_score IS NULL THEN 1 END) as null_hot_score_after,
    COUNT(CASE WHEN is_hot IS NULL THEN 1 END) as null_is_hot_after,
    COUNT(CASE WHEN hot_updated_at IS NULL THEN 1 END) as null_hot_updated_at_after
FROM posts;

-- 4. 전체 게시글의 HOT 상태 확인
SELECT 
    COUNT(*) as total_posts,
    COUNT(CASE WHEN hot_score = 0 THEN 1 END) as zero_score_posts,
    COUNT(CASE WHEN hot_score > 0 THEN 1 END) as positive_score_posts,
    COUNT(CASE WHEN is_hot = 1 THEN 1 END) as hot_posts,
    COUNT(CASE WHEN is_hot = 0 THEN 1 END) as not_hot_posts
FROM posts; 