# 공지사항 게시판 API 구현 명세서

## 📋 구현 개요

공지사항 게시판은 관리자만 작성할 수 있고, 모든 사용자가 읽을 수 있는 특별한 게시판입니다. 기존 게시판 시스템을 확장하여 공지사항 전용 API를 구현했습니다.

## 🎯 주요 기능

### 1. 권한 관리
- **읽기 권한**: 모든 사용자 (인증 불필요)
- **작성 권한**: 관리자만 (`@PreAuthorize("hasRole('ADMIN')")`)
- **수정 권한**: 관리자만
- **삭제 권한**: 관리자만

### 2. API 엔드포인트
- `GET /api/notices` - 공지사항 목록 조회
- `GET /api/notices/{id}` - 공지사항 상세 조회
- `POST /api/notices` - 공지사항 작성 (관리자만)
- `PUT /api/notices/{id}` - 공지사항 수정 (관리자만)
- `DELETE /api/notices/{id}` - 공지사항 삭제 (관리자만)
- `POST /api/notices/{id}/view` - 조회수 증가

## 🗄️ 데이터베이스 변경사항

### 1. categories 테이블에 공지사항 추가
```sql
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

-- 기존 카테고리들의 sort_order 조정
UPDATE categories SET sort_order = sort_order + 1 WHERE english_name != 'notice';
```

### 2. 공지사항 예시 데이터 추가
```sql
INSERT INTO posts (user_id, title, content, category_id, author_user_type, is_anonymous, anonymous_nickname, like_count, comment_count, view_count, report_count, is_hot, hot_score, created_at, updated_at) 
VALUES 
(
    (SELECT id FROM users WHERE user_type = '관리자' LIMIT 1),
    '서비스 이용 안내',
    '안녕하세요. 서비스 이용에 대한 안내사항입니다...',
    (SELECT id FROM categories WHERE english_name = 'notice'),
    '관리자',
    false,
    NULL,
    0, 0, 0, 0, false, 0,
    NOW(),
    NOW()
);
```

## 📁 구현된 파일들

### 1. 새로운 컨트롤러
- **파일**: `src/main/java/com/bootcampbox/server/controller/NoticeController.java`
- **기능**: 공지사항 전용 API 엔드포인트 제공
- **특징**: 
  - 관리자 권한 검증 (`@PreAuthorize("hasRole('ADMIN')")`)
  - 카테고리 검증 (공지사항인지 확인)
  - 자동 카테고리 설정 (작성 시 "NOTICE"로 강제 설정)

### 2. DTO 확장
- **파일**: `src/main/java/com/bootcampbox/server/dto/PostDto.java`
- **변경사항**:
  - `CreateRequest`에 `category` 필드 추가
  - `UpdateRequest`에 `category` 필드 추가
  - `setCategory()` 메서드 추가

### 3. 서비스 레이어 확장
- **파일**: `src/main/java/com/bootcampbox/server/service/PostService.java`
- **변경사항**:
  - `createPost(User user, PostDto.CreateRequest request)` 오버로드 추가
  - `updatePost(User user, Long postId, PostDto.UpdateRequest request)` 오버로드 추가
  - `deletePost(User user, Long postId)` 오버로드 추가
  - `mapCategoryToDbValue()` 메서드에 "NOTICE" 매핑 추가

### 4. 카테고리 서비스 확장
- **파일**: `src/main/java/com/bootcampbox/server/service/CategoryService.java`
- **변경사항**:
  - `getCategoryByEnglishName(String englishName)` 메서드 추가

### 5. 리포지토리 확장
- **파일**: `src/main/java/com/bootcampbox/server/repository/CategoryRepository.java`
- **변경사항**:
  - `findByEnglishName(String englishName)` 메서드 추가

### 6. 검색 조건 확장
- **파일**: `src/main/java/com/bootcampbox/server/dto/PostSearchCondition.java`
- **변경사항**:
  - `isValidCategory()` 메서드에 "NOTICE" 추가

## 🔧 기술적 구현 세부사항

### 1. 권한 관리
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponse<PostDto.Response>> createNotice(...)
```

### 2. 카테고리 강제 설정
```java
// 공지사항 카테고리로 강제 설정
request.setCategory("NOTICE");
```

### 3. 카테고리 검증
```java
// 공지사항 카테고리인지 확인
if (!"notice".equals(response.getCategory().getEnglishName())) {
    return ResponseEntity.badRequest().body(ApiResponse.error("공지사항이 아닙니다."));
}
```

### 4. 카테고리 매핑
```java
case "NOTICE":
    return "notice";
case "공지사항":
    return "notice";
```

## 📊 API 응답 구조

### 성공 응답 예시
```json
{
  "success": true,
  "message": "공지사항 목록 조회 성공",
  "data": {
    "posts": [...],
    "pagination": {
      "currentPage": 0,
      "totalPages": 1,
      "totalElements": 3,
      "size": 20,
      "hasNext": false,
      "hasPrevious": false
    },
    "searchInfo": {
      "searchKeyword": null,
      "category": "NOTICE",
      "resultCount": 3
    }
  }
}
```

### 에러 응답 예시
```json
{
  "success": false,
  "message": "접근 권한이 없습니다.",
  "data": null,
  "error": "AccessDeniedException"
}
```

## 🧪 테스트 및 검증

### 1. 테스트 스크립트
- **파일**: `TEST_NOTICE_API.sh`
- **기능**: 
  - 공지사항 목록/상세 조회 테스트
  - 관리자 권한 테스트
  - 일반 사용자 권한 제한 테스트
  - CRUD 작업 테스트

### 2. 테스트 시나리오
1. **인증 없이 공지사항 조회** ✅
2. **관리자 로그인 후 공지사항 작성** ✅
3. **일반 사용자가 공지사항 작성 시도 (권한 없음)** ✅
4. **공지사항 수정/삭제 (관리자만)** ✅
5. **조회수 증가 기능** ✅

## 📝 API 명세서

### 1. 공지사항 목록 조회
```
GET /api/notices
파라미터: search, sortBy, sortOrder, page, size
권한: 없음 (모든 사용자)
```

### 2. 공지사항 상세 조회
```
GET /api/notices/{id}
파라미터: id (경로)
권한: 없음 (모든 사용자)
```

### 3. 공지사항 작성
```
POST /api/notices
헤더: Authorization: Bearer {JWT_TOKEN}
본문: title, content, isAnonymous, authorUserType
권한: 관리자만
```

### 4. 공지사항 수정
```
PUT /api/notices/{id}
헤더: Authorization: Bearer {JWT_TOKEN}
본문: title, content, isAnonymous, authorUserType
권한: 관리자만
```

### 5. 공지사항 삭제
```
DELETE /api/notices/{id}
헤더: Authorization: Bearer {JWT_TOKEN}
권한: 관리자만
```

### 6. 조회수 증가
```
POST /api/notices/{id}/view
파라미터: id (경로)
권한: 없음 (모든 사용자)
```

## 🔒 보안 고려사항

### 1. 권한 검증
- Spring Security의 `@PreAuthorize` 어노테이션 사용
- JWT 토큰 기반 인증
- 역할 기반 접근 제어 (RBAC)

### 2. 데이터 검증
- 카테고리 강제 설정으로 데이터 무결성 보장
- 요청된 게시글이 실제 공지사항인지 검증
- 입력 데이터 유효성 검사

### 3. 에러 처리
- 적절한 HTTP 상태 코드 반환
- 일관된 에러 응답 형식
- 보안 정보 노출 방지

## 🚀 성능 최적화

### 1. 데이터베이스 인덱스
- 공지사항 카테고리별 조회 성능 향상
- 정렬 및 검색 성능 최적화

### 2. 캐싱 전략
- 공지사항 목록 캐싱 고려
- 조회수 증가 시 배치 처리 고려

## 📈 확장 가능성

### 1. 향후 추가 기능
- 공지사항 고정 기능
- 공지사항 알림 기능
- 공지사항 템플릿 기능
- 공지사항 스케줄링 기능

### 2. 관리자 기능
- 공지사항 통계 대시보드
- 공지사항 작성 히스토리
- 공지사항 승인 워크플로우

## 📋 구현 체크리스트

- [x] 데이터베이스 스키마 업데이트
- [x] 공지사항 카테고리 추가
- [x] 예시 데이터 추가
- [x] NoticeController 구현
- [x] PostDto 확장
- [x] PostService 확장
- [x] CategoryService 확장
- [x] CategoryRepository 확장
- [x] PostSearchCondition 확장
- [x] 권한 관리 구현
- [x] 카테고리 검증 구현
- [x] API 테스트 스크립트 작성
- [x] API 명세서 작성
- [x] 에러 처리 구현
- [x] 보안 검증 구현

## 🎉 결론

공지사항 게시판 API가 성공적으로 구현되었습니다. 관리자만 작성할 수 있고 모든 사용자가 읽을 수 있는 안전하고 효율적인 공지사항 시스템을 제공합니다. 기존 게시판 시스템과 완전히 호환되면서도 공지사항만의 특별한 권한 관리와 검증 로직을 갖추고 있습니다. 