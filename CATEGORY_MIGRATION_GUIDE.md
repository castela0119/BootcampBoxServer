# 카테고리 마이그레이션 가이드

## 📋 개요
기존 게시글들을 카테고리 시스템으로 분류하는 마이그레이션 가이드입니다.

## 🚀 실행 순서

### 1단계: 기본 카테고리 생성
```bash
# MySQL에 접속
mysql -u root -prookie123 rookie_db

# 기본 카테고리 생성 스크립트 실행
source CREATE_DEFAULT_CATEGORIES.sql
```

### 2단계: 기존 게시글 분류
```bash
# 기존 게시글들을 자유게시판으로 분류
source UPDATE_EXISTING_POSTS_CATEGORY.sql
```

### 3단계: 결과 확인
```sql
-- 카테고리별 게시글 수 확인
SELECT 
    c.name as category_name,
    COUNT(p.id) as post_count
FROM categories c
LEFT JOIN posts p ON c.id = p.category_id
WHERE c.is_active = true
GROUP BY c.id, c.name
ORDER BY c.sort_order;
```

## 📊 예상 결과

### 생성될 카테고리
| 카테고리명 | 설명 | 정렬순서 | 익명여부 |
|------------|------|----------|----------|
| 공지사항 | 중요한 공지사항 | 0 | false |
| 자유게시판 | 자유로운 이야기 | 1 | false |
| 질문게시판 | 궁금한 점 | 2 | false |
| 리뷰게시판 | 다양한 리뷰 | 3 | false |
| 익명게시판 | 익명 공간 | 4 | true |

### 기존 게시글 처리
- **모든 기존 게시글** → **자유게시판**으로 분류
- `category_id`가 `NULL`인 게시글들이 모두 업데이트됨

## ⚠️ 주의사항

1. **백업 필수**: 마이그레이션 전에 데이터베이스 백업을 반드시 수행하세요.
2. **테스트 환경**: 먼저 테스트 환경에서 실행해보세요.
3. **순서 준수**: 반드시 1단계 → 2단계 순서로 실행하세요.

## 🔧 수동 실행 방법

### MySQL 접속
```bash
mysql -u root -prookie123 rookie_db
```

### 카테고리 생성
```sql
INSERT INTO categories (name, is_anonymous, description, sort_order, is_active, created_at, updated_at)
VALUES ('자유게시판', false, '자유롭게 이야기를 나누는 공간입니다.', 1, true, NOW(), NOW());
```

### 기존 게시글 업데이트
```sql
UPDATE posts 
SET category_id = (SELECT id FROM categories WHERE name = '자유게시판' LIMIT 1)
WHERE category_id IS NULL;
```

## 📝 추가 작업

마이그레이션 완료 후 다음 작업을 진행하세요:

1. **PostService에 카테고리별 조회 기능 추가**
2. **PostController에 카테고리별 API 추가**
3. **새 게시글 작성 시 카테고리 선택 기능 구현**

## 🎯 완료 후 확인사항

- [ ] 모든 카테고리가 정상 생성됨
- [ ] 기존 게시글들이 자유게시판으로 분류됨
- [ ] `category_id`가 `NULL`인 게시글이 없음
- [ ] 카테고리별 게시글 수가 정상적으로 표시됨 