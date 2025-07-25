-- 카테고리별 검색 성능 향상을 위한 인덱스 추가
-- 실행 전 반드시 백업을 수행하세요!

-- 1. 카테고리별 검색을 위한 복합 인덱스
-- 카테고리 + 검색어 조합으로 빠른 검색 가능
CREATE INDEX idx_posts_category_search ON posts(category_id, title, content);

-- 2. 카테고리별 + 작성자 신분 조합 인덱스
CREATE INDEX idx_posts_category_author_type ON posts(category_id, author_user_type);

-- 3. 카테고리별 + 생성일자 정렬 인덱스
CREATE INDEX idx_posts_category_created_at ON posts(category_id, created_at DESC);

-- 4. 카테고리별 + 좋아요 수 정렬 인덱스
CREATE INDEX idx_posts_category_like_count ON posts(category_id, like_count DESC);

-- 5. 카테고리별 + 조회수 정렬 인덱스
CREATE INDEX idx_posts_category_view_count ON posts(category_id, view_count DESC);

-- 6. 카테고리별 + 댓글 수 정렬 인덱스
CREATE INDEX idx_posts_category_comment_count ON posts(category_id, comment_count DESC);

-- 7. 제목 검색을 위한 인덱스
CREATE INDEX idx_posts_title_search ON posts(title);

-- 8. 내용 검색을 위한 인덱스
CREATE INDEX idx_posts_content_search ON posts(content);

-- 9. 태그 검색을 위한 인덱스 (post_tags 테이블)
CREATE INDEX idx_post_tags_tag_name ON post_tags(tag_id);

-- 10. 카테고리 테이블의 영문명 인덱스
CREATE INDEX idx_categories_english_name ON categories(english_name);

-- 인덱스 생성 확인
SHOW INDEX FROM posts;
SHOW INDEX FROM post_tags;
SHOW INDEX FROM categories;

-- 인덱스 사용 현황 확인 (MySQL 8.0+)
-- SELECT * FROM information_schema.statistics 
-- WHERE table_schema = 'rookie_db' 
-- AND table_name IN ('posts', 'post_tags', 'categories'); 