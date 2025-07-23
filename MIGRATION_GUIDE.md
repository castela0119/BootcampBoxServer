# 카테고리 마이그레이션 가이드

## 📋 마이그레이션 개요

기존 카테고리 시스템을 새로운 5개 게시판으로 교체하는 마이그레이션 작업입니다.

### 🎯 목표
- 기존 카테고리 삭제
- 새로운 5개 카테고리 생성
- 모든 기존 게시글을 "커뮤니티 탭 게시판"으로 분류

### 📊 새로운 카테고리 구조
| ID | 카테고리명 | 정렬순서 | 설명 |
|----|------------|----------|------|
| 1 | 커뮤니티 탭 게시판 | 1 | 자유롭게 이야기를 나누는 공간 |
| 2 | 진로 상담 | 2 | 진로와 관련된 고민 상담 |
| 3 | 연애 상담 | 3 | 연애와 관련된 고민 상담 |
| 4 | 사건 사고 | 4 | 사건사고 관련 이야기 |
| 5 | 휴가 어때 | 5 | 휴가 후기 및 이야기 |

## 🚀 마이그레이션 실행

### 1. 마이그레이션 실행
```bash
# MySQL에 접속하여 마이그레이션 스크립트 실행
mysql -u root -prookie123 < MIGRATE_TO_NEW_CATEGORIES.sql
```

### 2. 마이그레이션 확인
```bash
# 카테고리 목록 확인
mysql -u root -prookie123 -e "USE rookie_db; SELECT id, name, sort_order FROM categories ORDER BY sort_order;"

# 게시글 분류 현황 확인
mysql -u root -prookie123 -e "USE rookie_db; SELECT c.name, COUNT(p.id) as post_count FROM categories c LEFT JOIN posts p ON c.id = p.category_id GROUP BY c.id, c.name ORDER BY c.sort_order;"
```

## ✅ 마이그레이션 후 확인사항

### 1. 카테고리 API 테스트
```bash
# 커뮤니티 탭 게시판 조회
curl -X GET "http://localhost:8080/api/posts/community?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 진로 상담 게시판 조회
curl -X GET "http://localhost:8080/api/posts/career?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 연애 상담 게시판 조회
curl -X GET "http://localhost:8080/api/posts/love?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 사건 사고 게시판 조회
curl -X GET "http://localhost:8080/api/posts/incident?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 휴가 어때 게시판 조회
curl -X GET "http://localhost:8080/api/posts/vacation?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 2. 게시글 작성 테스트
```bash
# 진로 상담 게시판에 게시글 작성
curl -X POST "http://localhost:8080/api/posts" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "진로 상담 테스트",
    "content": "진로 상담 내용입니다.",
    "categoryId": 2
  }'
```

## ⚠️ 주의사항

### 1. 백업 권장
마이그레이션 전에 데이터베이스 백업을 권장합니다:
```bash
# 백업 생성
mysqldump -u root -prookie123 rookie_db > backup_before_migration.sql
```

### 2. 서버 중지
마이그레이션 중에는 서버를 중지하는 것을 권장합니다:
```bash
# 서버 중지
pkill -f "RookiePxServerApplication"
```

### 3. 마이그레이션 후 서버 재시작
```bash
# 서버 재시작
./run-mysql.sh
```

## 🔄 롤백 방법

만약 문제가 발생하면 백업에서 복원할 수 있습니다:
```bash
# 롤백 실행
mysql -u root -prookie123 rookie_db < backup_before_migration.sql
```

## 📝 마이그레이션 결과 예상

### 마이그레이션 전
- 기존 카테고리: 자유게시판, 익명게시판, 육군게시판 등
- 게시글 분류: 대부분 NULL 또는 기존 카테고리

### 마이그레이션 후
- 새로운 카테고리: 5개 게시판
- 게시글 분류: 모든 게시글이 "커뮤니티 탭 게시판"으로 분류
- API 정상 작동: 카테고리별 조회 가능

## 🎯 다음 단계

1. 마이그레이션 실행
2. API 테스트
3. 클라이언트 연동
4. 새로운 게시글 작성 테스트 