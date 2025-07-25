-- 공지사항 게시글 예시 추가
-- 실행 전 반드시 백업을 수행하세요!

USE rookie_db;

-- 1. 관리자 사용자 확인
SELECT id, username, email, user_type FROM users WHERE user_type = '관리자';

-- 2. 공지사항 카테고리 ID 확인
SELECT id, name, english_name FROM categories WHERE english_name = 'notice';

-- 3. 공지사항 게시글 예시 추가
INSERT INTO posts (user_id, title, content, category_id, author_user_type, is_anonymous, anonymous_nickname, like_count, comment_count, view_count, report_count, is_hot, hot_score, created_at, updated_at) 
VALUES 
(
    (SELECT id FROM users WHERE user_type = '관리자' LIMIT 1),
    '서비스 이용 안내',
    '안녕하세요. 서비스 이용에 대한 안내사항입니다.\n\n1. 게시글 작성 시 매너를 지켜주세요.\n2. 개인정보 보호에 유의해주세요.\n3. 문의사항이 있으시면 관리자에게 연락해주세요.\n\n감사합니다.',
    (SELECT id FROM categories WHERE english_name = 'notice'),
    '관리자',
    false,
    NULL,
    0, 0, 0, 0, false, 0,
    NOW(),
    NOW()
),
(
    (SELECT id FROM users WHERE user_type = '관리자' LIMIT 1),
    '게시판 이용 규칙',
    '게시판 이용 규칙을 안내드립니다.\n\n■ 금지사항\n- 욕설 및 비방성 게시글\n- 스팸 및 광고성 게시글\n- 개인정보 노출\n- 저작권 침해\n\n■ 이용 규칙\n- 서로를 존중하는 마음으로 소통해주세요\n- 건전한 토론 문화를 만들어주세요\n- 도움이 되는 정보를 공유해주세요\n\n위반 시 게시글이 삭제되거나 계정이 제재될 수 있습니다.',
    (SELECT id FROM categories WHERE english_name = 'notice'),
    '관리자',
    false,
    NULL,
    0, 0, 0, 0, false, 0,
    NOW(),
    NOW()
),
(
    (SELECT id FROM users WHERE user_type = '관리자' LIMIT 1),
    '시스템 점검 안내',
    '시스템 점검 안내드립니다.\n\n■ 점검 일시: 2025년 7월 26일 (토) 02:00 ~ 06:00\n■ 점검 내용: 서버 성능 개선 및 보안 업데이트\n■ 영향 범위: 게시글 작성, 댓글 작성 일시 중단\n\n점검 시간 동안에는 서비스 이용이 제한될 수 있습니다.\n불편을 끼쳐 죄송합니다.',
    (SELECT id FROM categories WHERE english_name = 'notice'),
    '관리자',
    false,
    NULL,
    0, 0, 0, 0, false, 0,
    NOW(),
    NOW()
);

-- 4. 추가된 공지사항 게시글 확인
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

-- 5. 카테고리별 게시글 수 업데이트 확인
SELECT 
    c.name as category_name,
    c.english_name,
    COUNT(p.id) as post_count,
    c.sort_order
FROM categories c
LEFT JOIN posts p ON c.id = p.category_id
GROUP BY c.id, c.name, c.english_name, c.sort_order
ORDER BY c.sort_order ASC; 