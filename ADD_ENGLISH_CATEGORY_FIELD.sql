-- 카테고리 테이블에 영문 필드 추가 및 영문 값 설정

USE rookie_db;

-- 1. 영문 필드 추가
ALTER TABLE categories ADD COLUMN english_name VARCHAR(255) AFTER name;

-- 2. 각 카테고리에 영문 값 설정
UPDATE categories SET english_name = 'community' WHERE name = '커뮤니티 탭 게시판';
UPDATE categories SET english_name = 'career' WHERE name = '진로 상담';
UPDATE categories SET english_name = 'love' WHERE name = '연애 상담';
UPDATE categories SET english_name = 'incident' WHERE name = '사건 사고';
UPDATE categories SET english_name = 'vacation' WHERE name = '휴가 어때';

-- 3. 결과 확인
SELECT id, name, english_name, sort_order FROM categories ORDER BY sort_order; 