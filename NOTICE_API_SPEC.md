# 공지사항 API 명세서

## 개요
공지사항 게시판은 관리자만 작성할 수 있고, 모든 사용자가 읽을 수 있는 특별한 게시판입니다.

## 기본 정보
- **Base URL**: `/api/notices`
- **인증**: JWT 토큰 (관리자 권한이 필요한 작업의 경우)
- **권한**: 
  - 읽기: 모든 사용자
  - 작성/수정/삭제: 관리자만

## API 엔드포인트

### 1. 공지사항 목록 조회
**GET** `/api/notices`

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| search | String | X | - | 검색어 (제목, 내용에서 검색) |
| sortBy | String | X | createdAt | 정렬 기준 (createdAt, likeCount, viewCount, commentCount) |
| sortOrder | String | X | desc | 정렬 순서 (asc, desc) |
| page | int | X | 0 | 페이지 번호 (0부터 시작) |
| size | int | X | 20 | 페이지 크기 |

#### 응답 예시
```json
{
  "success": true,
  "message": "공지사항 목록 조회 성공",
  "data": {
    "posts": [
      {
        "id": 58,
        "title": "서비스 이용 안내",
        "content": "안녕하세요. 서비스 이용에 대한 안내사항입니다...",
        "user": {
          "id": 12,
          "nickname": "최고관리자",
          "userType": "관리자"
        },
        "createdAt": "2025-07-25T20:31:25",
        "updatedAt": "2025-07-25T20:31:25",
        "isAnonymous": false,
        "displayNickname": "최고관리자",
        "authorUserType": "관리자",
        "categoryId": 13,
        "categoryName": "공지사항",
        "likeCount": 0,
        "isLiked": false,
        "isBookmarked": false,
        "commentCount": 0,
        "viewCount": 0
      }
    ],
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

### 2. 공지사항 상세 조회
**GET** `/api/notices/{id}`

#### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| id | Long | O | 공지사항 ID |

#### 응답 예시
```json
{
  "success": true,
  "message": "공지사항 상세 조회 성공",
  "data": {
    "id": 58,
    "title": "서비스 이용 안내",
    "content": "안녕하세요. 서비스 이용에 대한 안내사항입니다...",
    "user": {
      "id": 12,
      "nickname": "최고관리자",
      "userType": "관리자"
    },
    "createdAt": "2025-07-25T20:31:25",
    "updatedAt": "2025-07-25T20:31:25",
    "isAnonymous": false,
    "displayNickname": "최고관리자",
    "authorUserType": "관리자",
    "categoryId": 13,
    "categoryName": "공지사항",
    "likeCount": 0,
    "isLiked": false,
    "isBookmarked": false,
    "commentCount": 0,
    "viewCount": 0
  }
}
```

### 3. 공지사항 작성 (관리자만)
**POST** `/api/notices`

#### 요청 헤더
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

#### 요청 본문
```json
{
  "title": "새로운 공지사항",
  "content": "이것은 테스트용 공지사항입니다.",
  "isAnonymous": false,
  "authorUserType": "관리자"
}
```

#### 응답 예시
```json
{
  "success": true,
  "message": "공지사항 작성 성공",
  "data": {
    "id": 61,
    "title": "새로운 공지사항",
    "content": "이것은 테스트용 공지사항입니다.",
    "user": {
      "id": 12,
      "nickname": "최고관리자",
      "userType": "관리자"
    },
    "createdAt": "2025-07-25T20:35:10",
    "updatedAt": "2025-07-25T20:35:10",
    "isAnonymous": false,
    "displayNickname": "최고관리자",
    "authorUserType": "관리자",
    "categoryId": 13,
    "categoryName": "공지사항",
    "likeCount": 0,
    "isLiked": false,
    "isBookmarked": false,
    "commentCount": 0,
    "viewCount": 0
  }
}
```

### 4. 공지사항 수정 (관리자만)
**PUT** `/api/notices/{id}`

#### 요청 헤더
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

#### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| id | Long | O | 공지사항 ID |

#### 요청 본문
```json
{
  "title": "수정된 공지사항",
  "content": "이것은 수정된 공지사항입니다.",
  "isAnonymous": false,
  "authorUserType": "관리자"
}
```

### 5. 공지사항 삭제 (관리자만)
**DELETE** `/api/notices/{id}`

#### 요청 헤더
```
Authorization: Bearer {JWT_TOKEN}
```

#### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| id | Long | O | 공지사항 ID |

#### 응답 예시
```json
{
  "success": true,
  "message": "공지사항 삭제 성공",
  "data": null
}
```

### 6. 공지사항 조회수 증가
**POST** `/api/notices/{id}/view`

#### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| id | Long | O | 공지사항 ID |

#### 응답 예시
```json
{
  "success": true,
  "message": "조회수 증가 성공",
  "data": null
}
```

## 에러 응답

### 권한 없음 (403 Forbidden)
```json
{
  "success": false,
  "message": "접근 권한이 없습니다.",
  "data": null,
  "error": "AccessDeniedException"
}
```

### 공지사항이 아닌 경우 (400 Bad Request)
```json
{
  "success": false,
  "message": "공지사항이 아닙니다.",
  "data": null,
  "error": "IllegalArgumentException"
}
```

### 게시글을 찾을 수 없는 경우 (400 Bad Request)
```json
{
  "success": false,
  "message": "게시글을 찾을 수 없습니다.",
  "data": null,
  "error": "IllegalArgumentException"
}
```

## 검색 및 정렬 옵션

### 정렬 기준 (sortBy)
- `createdAt`: 작성일시
- `likeCount`: 좋아요 수
- `viewCount`: 조회수
- `commentCount`: 댓글 수

### 정렬 순서 (sortOrder)
- `asc`: 오름차순
- `desc`: 내림차순

### 검색 기능
- 제목과 내용에서 검색어를 포함하는 공지사항을 찾습니다.
- 검색어는 2글자 이상 입력해야 합니다.

## 보안 고려사항

1. **관리자 권한 확인**: 공지사항 작성/수정/삭제는 `@PreAuthorize("hasRole('ADMIN')")` 어노테이션으로 보호됩니다.

2. **카테고리 강제 설정**: 공지사항 작성 시 카테고리가 자동으로 "NOTICE"로 설정됩니다.

3. **카테고리 검증**: 모든 공지사항 관련 API에서 요청된 게시글이 실제로 공지사항 카테고리인지 확인합니다.

## 테스트

공지사항 API 테스트는 `TEST_NOTICE_API.sh` 스크립트를 사용할 수 있습니다:

```bash
chmod +x TEST_NOTICE_API.sh
./TEST_NOTICE_API.sh
```

## 데이터베이스 스키마

### categories 테이블
```sql
INSERT INTO categories (name, english_name, description, is_anonymous, sort_order, is_active, created_at, updated_at) 
VALUES (
    '공지사항', 
    'notice', 
    '중요한 공지사항과 안내사항을 확인하는 공간', 
    false, 
    0,  -- 최상단에 표시
    true, 
    NOW(), 
    NOW()
);
```

### posts 테이블
공지사항은 일반 게시글과 동일한 구조를 사용하지만, `category_id`가 공지사항 카테고리를 참조합니다. 