-- 새로운 게시판 구조에 맞는 카테고리 생성 SQL 스크립트
-- 기존 게시글들은 모두 "커뮤니티 탭 게시판"으로 분류됩니다.

-- 1. 커뮤니티 탭 게시판 (기존 게시글들이 여기로 분류됨)
INSERT INTO categories (name, is_anonymous, description, sort_order, is_active, created_at, updated_at)
VALUES ('커뮤니티 탭 게시판', false, '자유롭게 이야기를 나누는 커뮤니티 공간입니다.', 1, true, NOW(), NOW());

-- 2. 진로 상담
INSERT INTO categories (name, is_anonymous, description, sort_order, is_active, created_at, updated_at)
VALUES ('진로 상담', false, '진로와 관련된 고민을 상담받는 공간입니다.', 2, true, NOW(), NOW());

-- 3. 연애 상담
INSERT INTO categories (name, is_anonymous, description, sort_order, is_active, created_at, updated_at)
VALUES ('연애 상담', false, '연애와 관련된 고민을 상담받는 공간입니다.', 3, true, NOW(), NOW());

-- 4. 사건 사고
INSERT INTO categories (name, is_anonymous, description, sort_order, is_active, created_at, updated_at)
VALUES ('사건 사고', false, '사건사고와 관련된 이야기를 나누는 공간입니다.', 4, true, NOW(), NOW());

-- 5. 휴가 어때
INSERT INTO categories (name, is_anonymous, description, sort_order, is_active, created_at, updated_at)
VALUES ('휴가 어때', false, '휴가 후기와 관련된 이야기를 나누는 공간입니다.', 5, true, NOW(), NOW());

-- 생성된 카테고리 확인
SELECT * FROM categories ORDER BY sort_order; 