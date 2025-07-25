# 공지사항 API 명세서 (기존 PostController 사용)

## 개요
공지사항 게시판은 기존 게시글 API를 사용하며, 관리자만 작성/수정/삭제할 수 있고, 모든 사용자가 읽을 수 있습니다.

## 기본 정보
- **Base URL**: `/api/posts`
- **인증**: JWT 토큰 (관리자 권한이 필요한 작업의 경우)
- **권한**: 
  - 읽기: 모든 사용자
  - 작성/수정/삭제: 관리자만 (공지사항 카테고리인 경우)

## API 엔드포인트

### 1. 공지사항 목록 조회
**GET** `/api/posts?category=NOTICE`

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| category | String | O | - | "NOTICE" 또는 "공지사항" |
| search | String | X | - | 검색어 (제목, 내용에서 검색) |
| sortBy | String | X | createdAt | 정렬 기준 (createdAt, likeCount, viewCount, commentCount) |
| sortOrder | String | X | desc | 정렬 순서 (asc, desc) |
| page | int | X | 0 | 페이지 번호 (0부터 시작) |
| size | int | X | 20 | 페이지 크기 |

#### 응답 예시
```json
{
  "success": true,
  "message": "카테고리별 게시글 목록 조회 성공",
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
        "category": {
          "id": 13,
          "name": "공지사항",
          "englishName": "notice",
          "description": "중요한 공지사항과 안내사항을 확인하는 공간"
        },
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
**GET** `/api/posts/{id}`

#### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| id | Long | O | 공지사항 ID |

#### 응답 예시
```json
{
  "success": true,
  "message": "게시글 조회 성공",
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
    "category": {
      "id": 13,
      "name": "공지사항",
      "englishName": "notice",
      "description": "중요한 공지사항과 안내사항을 확인하는 공간"
    },
    "likeCount": 0,
    "isLiked": false,
    "isBookmarked": false,
    "commentCount": 0,
    "viewCount": 0
  }
}
```

### 3. 공지사항 작성 (관리자만)
**POST** `/api/posts`

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
  "category": "NOTICE",
  "isAnonymous": false,
  "authorUserType": "관리자"
}
```

#### 응답 예시
```json
{
  "success": true,
  "message": "게시글이 성공적으로 작성되었습니다.",
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
    "category": {
      "id": 13,
      "name": "공지사항",
      "englishName": "notice",
      "description": "중요한 공지사항과 안내사항을 확인하는 공간"
    },
    "likeCount": 0,
    "isLiked": false,
    "isBookmarked": false,
    "commentCount": 0,
    "viewCount": 0
  }
}
```

### 4. 공지사항 수정 (관리자만)
**PUT** `/api/posts/{id}`

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
  "category": "NOTICE",
  "isAnonymous": false,
  "authorUserType": "관리자"
}
```

### 5. 공지사항 삭제 (관리자만)
**DELETE** `/api/posts/{id}`

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
  "message": "게시글이 성공적으로 삭제되었습니다.",
  "data": null
}
```

## 에러 응답

### 권한 없음 (403 Forbidden)
```json
{
  "success": false,
  "message": "공지사항은 관리자만 작성할 수 있습니다.",
  "data": null,
  "error": null
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

1. **권한 검증**: 공지사항 카테고리일 때 관리자 권한을 확인합니다.
2. **카테고리 검증**: 요청된 카테고리가 "NOTICE" 또는 "공지사항"인지 확인합니다.
3. **일관된 에러 처리**: 적절한 HTTP 상태 코드와 에러 메시지를 반환합니다.

## 테스트

공지사항 API 테스트는 `TEST_NOTICE_API.sh` 스크립트를 사용할 수 있습니다:

```bash
chmod +x TEST_NOTICE_API.sh
./TEST_NOTICE_API.sh
```

## 사용 예시

### 공지사항 목록 조회
```bash
curl -X GET "http://localhost:8080/api/posts?category=NOTICE&page=0&size=10"
```

### 공지사항 작성 (관리자)
```bash
curl -X POST "http://localhost:8080/api/posts" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "새로운 공지사항",
    "content": "공지사항 내용입니다.",
    "category": "NOTICE"
  }'
```

### 공지사항 검색
```bash
curl -X GET "http://localhost:8080/api/posts?category=NOTICE&search=안내&page=0&size=5"
``` 