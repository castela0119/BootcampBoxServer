-- 기존 게시글들을 기본 카테고리로 분류하는 SQL 스크립트
-- 기존 게시글들의 category_id가 NULL인 경우를 처리합니다.

-- 1. 커뮤니티 탭 게시판 ID 조회 (기존 게시글들이 여기로 분류됨)
SET @community_board_id = (SELECT id FROM categories WHERE name = '커뮤니티 탭 게시판' LIMIT 1);

-- 2. 기존 게시글들을 모두 커뮤니티 탭 게시판으로 분류
-- (category_id가 NULL인 게시글들만 업데이트)
UPDATE posts 
SET category_id = @community_board_id 
WHERE category_id IS NULL;

-- 3. 업데이트 결과 확인
SELECT 
    '업데이트된 게시글 수' as description,
    COUNT(*) as count
FROM posts 
WHERE category_id = @free_board_id;

-- 4. 카테고리별 게시글 수 확인
SELECT 
    c.name as category_name,
    COUNT(p.id) as post_count
FROM categories c
LEFT JOIN posts p ON c.id = p.category_id
WHERE c.is_active = true
GROUP BY c.id, c.name
ORDER BY c.sort_order;

-- 5. 여전히 category_id가 NULL인 게시글 확인 (있다면 문제가 있음)
SELECT 
    'NULL 카테고리 게시글 수' as description,
    COUNT(*) as count
FROM posts 
WHERE category_id IS NULL; 