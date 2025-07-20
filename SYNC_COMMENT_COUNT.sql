-- 게시글 댓글 수 동기화 스크립트
-- 기존 게시글들의 comment_count를 실제 댓글 수와 동기화

-- 1. 현재 상태 확인
SELECT 
    p.id,
    p.title,
    p.comment_count as db_comment_count,
    (SELECT COUNT(*) FROM comments WHERE post_id = p.id) as actual_comment_count
FROM posts p 
WHERE p.comment_count != (SELECT COUNT(*) FROM comments WHERE post_id = p.id)
ORDER BY p.id;

-- 2. 댓글 수 동기화
UPDATE posts p 
SET comment_count = (
    SELECT COUNT(*) 
    FROM comments 
    WHERE post_id = p.id
);

-- 3. 동기화 후 확인
SELECT 
    p.id,
    p.title,
    p.comment_count as db_comment_count,
    (SELECT COUNT(*) FROM comments WHERE post_id = p.id) as actual_comment_count
FROM posts p 
ORDER BY p.comment_count DESC
LIMIT 10;

-- 4. 전체 통계
SELECT 
    COUNT(*) as total_posts,
    SUM(comment_count) as total_comments_in_db,
    (SELECT COUNT(*) FROM comments) as total_comments_actual
FROM posts; 