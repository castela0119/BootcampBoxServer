-- 공지사항 게시판 추가
-- 실행 전 반드시 백업을 수행하세요!

USE rookie_db;

-- 1. 공지사항 카테고리 추가
INSERT INTO categories (name, english_name, description, is_anonymous, sort_order, is_active, created_at, updated_at) 
VALUES (
    '공지사항', 
    'notice', 
    '중요한 공지사항과 안내사항을 확인하는 공간', 
    false, 
    0,  -- 최상단에 표시되도록 sort_order를 0으로 설정
    true, 
    NOW(), 
    NOW()
);

-- 2. 추가된 카테고리 확인
SELECT * FROM categories WHERE english_name = 'notice';

-- 3. 기존 카테고리들의 sort_order 조정 (공지사항이 최상단에 오도록)
UPDATE categories SET sort_order = sort_order + 1 WHERE english_name != 'notice';

-- 4. 최종 카테고리 목록 확인
SELECT id, name, english_name, description, sort_order, is_active 
FROM categories 
ORDER BY sort_order ASC;

-- 5. 공지사항 게시글 예시 추가 (관리자만 작성 가능)
-- 먼저 관리자 사용자 ID 확인
SELECT id, username, email, user_type FROM users WHERE user_type = '관리자' LIMIT 1;

-- 공지사항 게시글 예시 (실제 관리자 ID로 수정 필요)
-- INSERT INTO posts (user_id, title, content, category_id, author_user_type, is_anonymous, anonymous_nickname, created_at, updated_at)
-- VALUES (
--     (SELECT id FROM users WHERE user_type = '관리자' LIMIT 1),
--     '서비스 이용 안내',
--     '안녕하세요. 서비스 이용에 대한 안내사항입니다.\n\n1. 게시글 작성 시 매너를 지켜주세요.\n2. 개인정보 보호에 유의해주세요.\n3. 문의사항이 있으시면 관리자에게 연락해주세요.\n\n감사합니다.',
--     (SELECT id FROM categories WHERE english_name = 'notice'),
--     '관리자',
--     false,
--     NULL,
--     NOW(),
--     NOW()
-- );

-- 6. 공지사항 게시글 목록 확인
SELECT 
    p.id,
    p.title,
    p.content,
    u.nickname as author,
    c.name as category_name,
    p.created_at,
    p.like_count,
    p.comment_count,
    p.view_count
FROM posts p
JOIN users u ON p.user_id = u.id
JOIN categories c ON p.category_id = c.id
WHERE c.english_name = 'notice'
ORDER BY p.created_at DESC;

-- 7. 카테고리별 게시글 수 통계
SELECT 
    c.name as category_name,
    c.english_name,
    COUNT(p.id) as post_count,
    c.sort_order
FROM categories c
LEFT JOIN posts p ON c.id = p.category_id
GROUP BY c.id, c.name, c.english_name, c.sort_order
ORDER BY c.sort_order ASC; 