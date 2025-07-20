# Rookie PX Server

Spring Boot 기반의 군인 커뮤니티 서버

## 🎯 프로젝트 개요
- **프로젝트명**: Rookie PX Server (Spring Boot 3.5.3)
- **언어**: Java 17
- **데이터베이스**: MySQL 8.0
- **빌드 도구**: Gradle
- **주요 기능**: 회원가입/로그인, 커뮤니티, PX 상품, 알림

## 📋 개발 규칙

### Git 작업 규칙
- **Git 명령어 실행**: `git add`, `git commit`, `git push` 명령어는 사용자가 직접 요청할 때만 실행
- **자동 커밋 금지**: 코드 수정 후 자동으로 Git 작업을 진행하지 않음
- **사용자 승인**: 모든 Git 관련 작업은 사용자의 명시적 승인 후 진행
- **Commit/Push 요청**: 사용자가 "commit", "push", "커밋", "푸시" 등의 키워드로 요청할 때만 실행
- **자동 실행 금지**: 코드 수정, 빌드, 테스트 후에도 자동으로 Git 작업을 수행하지 않음

## 🛠️ 기술 스택

- **Backend**: Spring Boot 3.5.3, Java 17
- **Database**: MySQL 8.0
- **Build Tool**: Gradle
- **Authentication**: JWT
- **ORM**: JPA/Hibernate

## 🎯 API 엔드포인트

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
- `POST /api/posts/{id}/toggle-like` - 게시글 좋아요 토글
- `POST /api/posts/{id}/bookmark` - 게시글 북마크

### 댓글
- `GET /api/posts/{postId}/comments` - 댓글 목록
- `POST /api/posts/{postId}/comments` - 댓글 작성
- `PATCH /api/posts/comments/{commentId}` - 댓글 수정
- `DELETE /api/posts/comments/{commentId}` - 댓글 삭제
- `POST /api/posts/comments/{commentId}/toggle-like` - 댓글 좋아요 토글

## 🗄️ 데이터베이스

### 주요 테이블
- `users` - 사용자 정보
- `posts` - 게시글
- `comments` - 댓글
- `post_likes` - 게시글 좋아요
- `comment_likes` - 댓글 좋아요
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

### 좋아요 토글 응답
```json
{
  "message": "게시글에 좋아요를 눌렀습니다.",
  "likeCount": 5,
  "isLiked": true,
  "isBookmarked": false,
  "success": true
}
```

## 🔧 개발 환경

- **IDE**: IntelliJ IDEA, VS Code
- **Database**: MySQL Workbench
- **API Testing**: Postman
- **Version Control**: Git

## 📝 코딩 규칙

### 1. Java 코딩 스타일
- **클래스명**: PascalCase (예: `UserService`, `DuplicateCheckController`)
- **메서드명**: camelCase (예: `checkEmailDuplicate`, `createUser`)
- **변수명**: camelCase (예: `userEmail`, `isVerified`)
- **상수명**: UPPER_SNAKE_CASE (예: `JWT_SECRET`, `MAX_RETRY_COUNT`)
- **패키지명**: 소문자 (예: `com.bootcampbox.server.service`)

### 2. 파일 구조
```
src/main/java/com/bootcampbox/server/
├── config/          # 설정 클래스들
├── controller/      # REST API 컨트롤러
├── domain/          # JPA 엔티티
├── dto/            # 데이터 전송 객체
├── repository/     # JPA 리포지토리
├── service/        # 비즈니스 로직
└── util/           # 유틸리티 클래스
```

### 3. 네이밍 컨벤션
- **Controller**: `*Controller` (예: `AuthController`, `UserController`)
- **Service**: `*Service` (예: `UserService`, `AuthService`)
- **Repository**: `*Repository` (예: `UserRepository`, `PostRepository`)
- **Entity**: 단수형 (예: `User`, `Post`, `Comment`)
- **DTO**: `*Dto` (예: `UserDto`, `LoginRequestDto`)

## 🛡️ 에러 처리 규칙

### 1. 예외 처리 패턴
```java
// 컨트롤러에서 예외 처리
@PostMapping("/signup")
public ResponseEntity<?> signUp(@Valid @RequestBody UserDto.SignUpRequest request) {
    try {
        log.info("회원가입 요청: {}", request.getEmail());
        UserDto.SignUpResponse response = userService.signUp(request);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        log.error("회원가입 오류: ", e);
        return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
    }
}
```

### 2. 검증 예외 처리
```java
// 필수 필드 검증
@NotBlank(message = "이메일은 필수 입력값입니다.")
@Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", 
         message = "이메일 형식이 올바르지 않습니다.")
private String email;
```

## 📝 로깅 규칙

### 1. 로그 레벨 사용
- **ERROR**: 예외 발생, 시스템 오류
- **WARN**: 경고 상황, 예상치 못한 상태
- **INFO**: 중요한 비즈니스 이벤트
- **DEBUG**: 디버깅 정보, SQL 쿼리
- **TRACE**: 상세한 실행 흐름

### 2. 로그 메시지 형식
```java
// 요청 시작
log.info("=== 회원가입 요청 시작 ===");
log.info("요청 이메일: {}", request.getEmail());

// 처리 과정
log.info("이메일 중복확인 완료: {}", isDuplicate);

// 요청 완료
log.info("=== 회원가입 요청 완료 ===");
```

## 🌱 환경변수 관리 규칙

### 1. 환경변수 파일
- 환경변수는 `.env`, `env.sh`, 또는 `env-example.sh` 파일로 관리한다.
- 실제 배포/개발 환경에서는 `.env` 또는 `env.sh` 파일을 사용하고, 예시/템플릿은 `env-example.sh`로 제공한다.
- 환경변수 파일에는 DB, JWT, 서버 포트, 로깅 등 모든 민감 정보와 설정값을 포함한다.

### 2. 환경변수 적용 방법
- 서버 실행 전 반드시 환경변수를 로드해야 한다.
- `source env.sh` 또는 `source ./load-env.sh` 명령어로 환경변수를 적용한다.
- 환경변수 파일이 없을 경우, `env-example.sh`를 복사하여 사용한다.
  ```bash
  cp env-example.sh env.sh
  vi env.sh # 실제 값으로 수정
  source env.sh
  ```

### 3. 환경변수 주요 항목 예시
```bash
# Database
export DB_URL=jdbc:mysql://localhost:3306/rookie_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
export DB_USERNAME=root
export DB_PASSWORD=rookie123

# JWT
export JWT_SECRET=local-secret-key-for-development-only
export JWT_EXPIRATION=86400000

# Server
export SERVER_PORT=8080
export SPRING_PROFILES_ACTIVE=local
```

## ⚠️ 자주 발생하는 에러 및 해결방법

### 1. 데이터베이스 연결 오류
**에러**: `Communications link failure`
**원인**: MySQL 서버가 실행되지 않음
**해결**:
```bash
# MySQL 서비스 시작
brew services start mysql

# 연결 확인
mysql -u root -prookie123 -e "SELECT 1;"
```

### 2. 테이블 없음 오류
**에러**: `Table 'rookie_db.users' doesn't exist`
**원인**: DDL_AUTO 설정 문제
**해결**:
```properties
# application-local.properties
spring.jpa.hibernate.ddl-auto=create
```

### 3. JPA 엔티티 인식 실패
**에러**: `Not a managed type`
**원인**: 패키지 스캔 문제
**해결**:
```java
@SpringBootApplication
@EntityScan("com.bootcampbox.server.domain")
public class RookiePxServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RookiePxServerApplication.class, args);
    }
}
```

### 4. 포트 충돌 오류
**에러**: `Port 8080 is already in use`
**해결**:
```bash
# 포트 사용 중인 프로세스 확인
lsof -i :8080

# 프로세스 종료
kill -9 <PID>

# 또는 다른 포트 사용
export SERVER_PORT=8081
```

### 5. JWT 토큰 오류
**에러**: `JWT signature does not match`
**원인**: JWT_SECRET 불일치
**해결**:
```bash
# 환경변수 확인
echo $JWT_SECRET

# 재설정
export JWT_SECRET=local-secret-key-for-development-only
```

## 🚀 API 개발 규칙

### 1. REST API 설계
```java
// GET: 조회
@GetMapping("/api/users/{id}")
public ResponseEntity<UserDto> getUser(@PathVariable Long id)

// POST: 생성
@PostMapping("/api/auth/signup")
public ResponseEntity<UserDto> signUp(@RequestBody UserDto.SignUpRequest request)

// PUT: 전체 수정
@PutMapping("/api/users/{id}")
public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto.UpdateRequest request)

// PATCH: 부분 수정
@PatchMapping("/api/users/{id}")
public ResponseEntity<UserDto> partialUpdateUser(@PathVariable Long id, @RequestBody UserDto.PartialUpdateRequest request)

// DELETE: 삭제
@DeleteMapping("/api/users/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id)
```

### 2. 응답 형식 통일
```java
// 성공 응답
{
  "message": "회원가입이 완료되었습니다.",
  "success": true,
  "data": { ... }
}

// 에러 응답
{
  "message": "이미 가입된 이메일입니다.",
  "success": false,
  "error": "DUPLICATE_EMAIL"
}
```

### 3. 보안 설정
```java
// 인증 필요 API
@PreAuthorize("hasRole('USER')")
@GetMapping("/api/user/me")
public ResponseEntity<UserDto> getMyInfo()

// 관리자 전용 API
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/admin/users")
public ResponseEntity<AdminDto.UserListResponse> getAllUsers()
```

## 📚 유용한 명령어

### 1. 서버 관리
```bash
# 서버 시작
./run-local.sh

# 서버 중지
pkill -f "RookiePxServerApplication"

# 로그 확인
tail -f logs/application.log
```

### 2. 데이터베이스 관리
```bash
# MySQL 접속
mysql -u root -prookie123

# 데이터베이스 목록
SHOW DATABASES;

# 테이블 목록
USE rookie_db;
SHOW TABLES;
```

### 3. API 테스트
```bash
# 이메일 중복확인
curl -X POST http://localhost:8080/api/auth/check-duplicate \
  -H "Content-Type: application/json" \
  -d '{"type": "email", "value": "test@example.com"}'

# 회원가입
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123", "nickname": "testuser"}'
```

## 🔍 디버깅 팁

### 1. 로그 레벨 조정
```properties
# 상세한 SQL 로그
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Spring Security 로그
logging.level.org.springframework.security=DEBUG
```

### 2. 프로파일 사용
```bash
# 로컬 프로파일
export SPRING_PROFILES_ACTIVE=local

# 운영 프로파일
export SPRING_PROFILES_ACTIVE=prod
```

### 3. 데이터베이스 모니터링
```sql
-- 활성 연결 확인
SHOW PROCESSLIST;

-- 테이블 상태 확인
SHOW TABLE STATUS;
```

## �� 라이선스

MIT License
