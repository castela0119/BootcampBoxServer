-- HOT 점수 null 값 업데이트 스크립트
-- 기존 데이터의 hot_score가 null인 경우 0으로 설정

-- 1. 현재 null 값 개수 확인
SELECT COUNT(*) as null_count FROM posts WHERE hot_score IS NULL;

-- 2. null 값을 0으로 업데이트
UPDATE posts SET hot_score = 0 WHERE hot_score IS NULL;

-- 3. 업데이트 후 확인
SELECT COUNT(*) as null_count_after FROM posts WHERE hot_score IS NULL;

-- 4. 전체 게시글의 hot_score 상태 확인
SELECT 
    COUNT(*) as total_posts,
    COUNT(CASE WHEN hot_score = 0 THEN 1 END) as zero_score_posts,
    COUNT(CASE WHEN hot_score > 0 THEN 1 END) as positive_score_posts,
    COUNT(CASE WHEN is_hot = 1 THEN 1 END) as hot_posts
FROM posts; 