# Rookie PX Server

Spring Boot 기반의 군인 커뮤니티 서버

## �� 주요 기능

- **회원가입/로그인**: JWT 기반 인증
- **커뮤니티**: 게시글, 댓글, 좋아요, 북마크
- **마이페이지**: 사용자 정보, 활동 통계
- **관리자 기능**: 사용자/게시글/댓글 관리

## 🛠️ 기술 스택

- **Backend**: Spring Boot 3.5.3, Java 17
- **Database**: MySQL 8.0
- **Build Tool**: Gradle
- **Authentication**: JWT
- **ORM**: JPA/Hibernate

## �� API 엔드포인트

### 인증
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인

### 마이페이지
- `GET /api/user/me` - 내 정보 조회
- `PATCH /api/user/me` - 내 정보 수정
- `GET /api/user/me/posts` - 내가 쓴 글 목록
- `GET /api/user/me/liked-posts` - 내가 좋아요한 글 목록
- `GET /api/user/me/bookmarked-posts` - 내가 북마크한 글 목록

### 커뮤니티
- `GET /api/posts` - 게시글 목록
- `POST /api/posts` - 게시글 작성
- `GET /api/posts/{id}` - 게시글 상세
- `POST /api/posts/{id}/like` - 게시글 좋아요
- `POST /api/posts/{id}/bookmark` - 게시글 북마크

## ��️ 데이터베이스

### 주요 테이블
- `users` - 사용자 정보
- `posts` - 게시글
- `comments` - 댓글
- `post_likes` - 게시글 좋아요
- `bookmarks` - 북마크

### 활동 통계 필드
- `total_posts` - 작성한 게시글 수
- `total_comments` - 작성한 댓글 수
- `total_received_likes` - 받은 좋아요 총합
- `total_bookmarks` - 북마크한 게시글 수

## 🚀 실행 방법

### 1. 환경 설정
```bash
# 환경변수 파일 복사
cp env-example.sh env.sh

# 환경변수 수정
vi env.sh

# 환경변수 적용
source env.sh
```

### 2. 데이터베이스 설정
```bash
# MySQL 서비스 시작
brew services start mysql

# 데이터베이스 생성
mysql -u root -prookie123 -e "CREATE DATABASE IF NOT EXISTS rookie_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 3. 서버 실행
```bash
# 로컬 환경 실행
./run-local.sh

# 또는 MySQL 환경 실행
./run-mysql.sh
```

## 📊 API 응답 예시

### 마이페이지 정보
```json
{
  "id": 1,
  "nickname": "domo",
  "postCount": 5,
  "commentCount": 12,
  "total_received_likes": 23,
  "bookmarkCount": 8,
  "lastActivityAt": "2025-07-12T18:30:00"
}
```

## 🔧 개발 환경

- **IDE**: IntelliJ IDEA, VS Code
- **Database**: MySQL Workbench
- **API Testing**: Postman
- **Version Control**: Git

## �� 라이선스

MIT License
