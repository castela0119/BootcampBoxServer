-- 새로운 5개 카테고리로 마이그레이션하는 SQL 스크립트
-- 기존 카테고리를 삭제하고 새로운 카테고리로 교체

USE rookie_db;

-- 1. 기존 카테고리 삭제 (새로운 카테고리로 교체)
DELETE FROM categories;

-- 2. 새로운 5개 카테고리 생성
INSERT INTO categories (name, sort_order, created_at, updated_at) VALUES
('커뮤니티 탭 게시판', 1, NOW(), NOW()),
('진로 상담', 2, NOW(), NOW()),
('연애 상담', 3, NOW(), NOW()),
('사건 사고', 4, NOW(), NOW()),
('휴가 어때', 5, NOW(), NOW());

-- 3. 기존 게시글들을 기본 카테고리(커뮤니티 탭 게시판)로 분류
-- 카테고리가 NULL이거나 기존 카테고리인 게시글들을 모두 커뮤니티 탭 게시판으로 설정
UPDATE posts 
SET category_id = (SELECT id FROM categories WHERE name = '커뮤니티 탭 게시판')
WHERE category_id IS NULL 
   OR category_id IN (SELECT id FROM categories WHERE name IN ('자유게시판', '익명게시판', '육군게시판', '해군게시판', '해병대게시판', '공군게시판', '고민상담'));

-- 4. 마이그레이션 결과 확인
SELECT '=== 새로운 카테고리 목록 ===' as info;
SELECT id, name, sort_order FROM categories ORDER BY sort_order;

SELECT '=== 게시글 카테고리 분류 현황 ===' as info;
SELECT 
    c.name as category_name,
    COUNT(p.id) as post_count
FROM categories c
LEFT JOIN posts p ON c.id = p.category_id
GROUP BY c.id, c.name
ORDER BY c.sort_order;

SELECT '=== 카테고리별 게시글 샘플 ===' as info;
SELECT 
    p.id,
    p.title,
    c.name as category_name,
    p.created_at
FROM posts p
JOIN categories c ON p.category_id = c.id
ORDER BY p.created_at DESC
LIMIT 10; 