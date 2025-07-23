# 게시판별 카테고리 API 명세서

## 📋 개요
게시판별로 게시글을 분류하고 관리하는 API 시스템입니다. 5개의 게시판으로 구성되어 있으며, 각 게시판별로 독립적인 게시글 관리가 가능합니다.

## 🏗️ 게시판 구조

### 게시판 종류
| 게시판명 | 카테고리명 | 영문명 | 설명 | 정렬순서 |
|----------|------------|--------|------|----------|
| 커뮤니티 탭 게시판 | 커뮤니티 탭 게시판 | `community` | 자유롭게 이야기를 나누는 커뮤니티 공간 | 1 |
| 진로 상담 | 진로 상담 | `career` | 진로와 관련된 고민을 상담받는 공간 | 2 |
| 연애 상담 | 연애 상담 | `love` | 연애와 관련된 고민을 상담받는 공간 | 3 |
| 사건 사고 | 사건 사고 | `incident` | 사건사고와 관련된 이야기를 나누는 공간 | 4 |
| 휴가 어때 | 휴가 어때 | `vacation` | 휴가 후기와 관련된 이야기를 나누는 공간 | 5 |

## 🔗 기본 경로
```
/api/posts
```

## 📚 API 엔드포인트

### 1. 커뮤니티 탭 게시판

**GET** `/api/posts/community`

커뮤니티 탭 게시판의 게시글 목록을 조회합니다.

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | int | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | int | ❌ | 20 | 페이지당 게시글 수 |
| sortBy | String | ❌ | - | 정렬 기준 (likes, views, comments, created) |
| sortOrder | String | ❌ | desc | 정렬 순서 (asc, desc) |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/community?page=0&size=10&sortBy=likes&sortOrder=desc" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 예시
```json
{
  "content": [
    {
      "id": 1,
      "title": "커뮤니티 게시글 제목",
      "content": "게시글 내용",
      "user": {
        "id": 1,
        "username": "user1",
        "nickname": "사용자1"
      },
      "categoryId": 1,
      "categoryName": "커뮤니티 탭 게시판",
      "createdAt": "2024-01-15T10:30:00",
      "likeCount": 5,
      "commentCount": 3,
      "viewCount": 100,
      "isLiked": false,
      "isBookmarked": false,
      "tagNames": ["태그1", "태그2"]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 50,
  "totalPages": 5,
  "last": false,
  "first": true,
  "numberOfElements": 10
}
```

---

### 2. 진로 상담 게시판

**GET** `/api/posts/career`

진로 상담 게시판의 게시글 목록을 조회합니다.

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | int | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | int | ❌ | 20 | 페이지당 게시글 수 |
| sortBy | String | ❌ | - | 정렬 기준 (likes, views, comments, created) |
| sortOrder | String | ❌ | desc | 정렬 순서 (asc, desc) |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/career?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

---

### 3. 연애 상담 게시판

**GET** `/api/posts/love`

연애 상담 게시판의 게시글 목록을 조회합니다.

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | int | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | int | ❌ | 20 | 페이지당 게시글 수 |
| sortBy | String | ❌ | - | 정렬 기준 (likes, views, comments, created) |
| sortOrder | String | ❌ | desc | 정렬 순서 (asc, desc) |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/love?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

---

### 4. 사건 사고 게시판

**GET** `/api/posts/incident`

사건 사고 게시판의 게시글 목록을 조회합니다.

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | int | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | int | ❌ | 20 | 페이지당 게시글 수 |
| sortBy | String | ❌ | - | 정렬 기준 (likes, views, comments, created) |
| sortOrder | String | ❌ | desc | 정렬 순서 (asc, desc) |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/incident?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

---

### 5. 휴가 어때 게시판

**GET** `/api/posts/vacation`

휴가 어때 게시판의 게시글 목록을 조회합니다.

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | int | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | int | ❌ | 20 | 페이지당 게시글 수 |
| sortBy | String | ❌ | - | 정렬 기준 (likes, views, comments, created) |
| sortOrder | String | ❌ | desc | 정렬 순서 (asc, desc) |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/vacation?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

---

### 6. 범용 카테고리 API

**GET** `/api/posts/category/{categoryId}`

카테고리 ID로 특정 게시판의 게시글 목록을 조회합니다.

#### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| categoryId | Long | ✅ | 카테고리 ID |

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | int | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | int | ❌ | 20 | 페이지당 게시글 수 |
| sortBy | String | ❌ | - | 정렬 기준 (likes, views, comments, created) |
| sortOrder | String | ❌ | desc | 정렬 순서 (asc, desc) |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/category/2?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

---

### 7. 영문 카테고리 API

**GET** `/api/posts/category/english/{englishName}`

영문 카테고리명으로 특정 게시판의 게시글 목록을 조회합니다.

#### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| englishName | String | ✅ | 영문 카테고리명 (community, career, love, incident, vacation) |

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | int | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | int | ❌ | 20 | 페이지당 게시글 수 |
| sortBy | String | ❌ | - | 정렬 기준 (likes, views, comments, created) |
| sortOrder | String | ❌ | desc | 정렬 순서 (asc, desc) |

#### 영문 카테고리명 매핑
| 영문명 | 한글명 | 카테고리 ID |
|--------|--------|-------------|
| community | 커뮤니티 탭 게시판 | 8 |
| career | 진로 상담 | 9 |
| love | 연애 상담 | 10 |
| incident | 사건 사고 | 11 |
| vacation | 휴가 어때 | 12 |

#### 요청 예시
```bash
# 커뮤니티 탭 게시판 조회
curl -X GET "http://localhost:8080/api/posts/category/english/community?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 진로 상담 게시판 조회
curl -X GET "http://localhost:8080/api/posts/category/english/career?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 연애 상담 게시판 조회
curl -X GET "http://localhost:8080/api/posts/category/english/love?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 예시
```json
{
  "content": [
    {
      "id": 1,
      "title": "커뮤니티 게시글 제목",
      "content": "게시글 내용",
      "user": {
        "id": 1,
        "nickname": "사용자1",
        "userType": "ROOKIE"
      },
      "categoryId": 8,
      "categoryName": "커뮤니티 탭 게시판",
      "createdAt": "2024-01-15T10:30:00",
      "likeCount": 5,
      "commentCount": 3,
      "viewCount": 100,
      "isLiked": false,
      "isBookmarked": false,
      "tagNames": ["태그1", "태그2"]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 45,
  "totalPages": 5,
  "last": false,
  "first": true,
  "numberOfElements": 10
}
```

---

### 8. 게시글 작성 (카테고리 지정)

**POST** `/api/posts`

특정 게시판에 게시글을 작성합니다.

#### 요청 본문
```json
{
  "title": "게시글 제목",
  "content": "게시글 내용",
  "categoryId": 2,
  "tagNames": ["태그1", "태그2"],
  "isAnonymous": false,
  "authorUserType": "ROOKIE"
}
```

#### 요청 필드
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| title | String | ✅ | 게시글 제목 |
| content | String | ✅ | 게시글 내용 |
| categoryId | Long | ❌ | 카테고리 ID (미지정 시 기본값: 커뮤니티 탭 게시판) |
| tagNames | List<String> | ❌ | 태그명 목록 |
| isAnonymous | boolean | ❌ | 익명 여부 (기본값: false) |
| authorUserType | String | ❌ | 작성 당시 사용자 신분 |

#### 카테고리 ID 매핑
| 카테고리 ID | 게시판명 | 영문명 |
|-------------|----------|--------|
| 8 | 커뮤니티 탭 게시판 | community |
| 9 | 진로 상담 | career |
| 10 | 연애 상담 | love |
| 11 | 사건 사고 | incident |
| 12 | 휴가 어때 | vacation |

#### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/posts" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "진로 상담 제목",
    "content": "진로 상담 내용",
    "categoryId": 9,
    "tagNames": ["진로", "상담"],
    "isAnonymous": false
  }'
```

#### 응답 예시
```json
{
  "id": 1,
  "title": "진로 상담 제목",
  "content": "진로 상담 내용",
  "user": {
    "id": 1,
    "username": "user1",
    "nickname": "사용자1"
  },
  "categoryId": 9,
  "categoryName": "진로 상담",
  "createdAt": "2024-01-15T10:30:00",
  "likeCount": 0,
  "commentCount": 0,
  "viewCount": 0,
  "isLiked": false,
  "isBookmarked": false,
  "tagNames": ["진로", "상담"]
}
```

---

## 🔐 인증 요구사항

모든 API 엔드포인트는 JWT 토큰 인증이 필요합니다.

### 인증 헤더
```
Authorization: Bearer {JWT_TOKEN}
```

### 인증 실패 응답
```json
{
  "message": "인증이 필요합니다.",
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "details": "유효하지 않은 토큰입니다."
  }
}
```

---

## 📊 응답 코드

| HTTP 상태 코드 | 설명 |
|----------------|------|
| 200 | 성공 |
| 400 | 잘못된 요청 |
| 401 | 인증 실패 |
| 404 | 게시글 또는 카테고리를 찾을 수 없음 |
| 500 | 서버 내부 오류 |

---

## 🏗️ 데이터 모델

### PostDto.Response
```json
{
  "id": "Long",
  "title": "String",
  "content": "String",
  "user": "UserDto.SimpleUserResponse",
  "categoryId": "Long",
  "categoryName": "String",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime",
  "isAnonymous": "boolean",
  "anonymousNickname": "String",
  "displayNickname": "String",
  "canBeModified": "boolean",
  "canBeDeleted": "boolean",
  "authorUserType": "String",
  "tagNames": "List<String>",
  "likeCount": "int",
  "isLiked": "boolean",
  "isBookmarked": "boolean",
  "commentCount": "int",
  "viewCount": "int"
}
```

### PostDto.CreateRequest
```json
{
  "title": "String (필수)",
  "content": "String (필수)",
  "categoryId": "Long (선택)",
  "tagNames": "List<String> (선택)",
  "isAnonymous": "boolean (선택, 기본값: false)",
  "authorUserType": "String (선택)"
}
```

---

## 🔄 정렬 옵션

### sortBy 파라미터
| 값 | 설명 |
|----|------|
| likes | 좋아요 수 순 |
| views | 조회수 순 |
| comments | 댓글 수 순 |
| created | 작성일 순 (기본값) |

### sortOrder 파라미터
| 값 | 설명 |
|----|------|
| asc | 오름차순 |
| desc | 내림차순 (기본값) |

---

## 🧪 테스트 예시

### 전체 테스트 시나리오
```bash
# 1. 커뮤니티 탭 게시판 조회
curl -X GET "http://localhost:8080/api/posts/community?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 2. 진로 상담 게시판에 게시글 작성
curl -X POST "http://localhost:8080/api/posts" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "진로 상담 제목",
    "content": "진로 상담 내용",
    "categoryId": 9
  }'

# 3. 진로 상담 게시판 조회
curl -X GET "http://localhost:8080/api/posts/career?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 4. 카테고리 ID로 조회
curl -X GET "http://localhost:8080/api/posts/category/9?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 5. 영문 카테고리명으로 조회
curl -X GET "http://localhost:8080/api/posts/category/english/career?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 5. 인기순 정렬로 조회
curl -X GET "http://localhost:8080/api/posts/community?page=0&size=10&sortBy=likes&sortOrder=desc" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

---

## 📝 참고사항

- **기본 카테고리**: 게시글 작성 시 `categoryId`를 지정하지 않으면 "커뮤니티 탭 게시판"으로 자동 분류됩니다.
- **기존 게시글**: 마이그레이션 전에 작성된 모든 게시글은 "커뮤니티 탭 게시판"으로 분류됩니다.
- **영문 카테고리명**: 클라이언트에서 더 편리한 API 호출을 위해 영문 카테고리명을 사용할 수 있습니다.
- **페이징**: 모든 목록 조회 API는 페이징을 지원합니다.
- **정렬**: 좋아요, 조회수, 댓글수, 작성일 기준으로 정렬 가능합니다.
- **태그**: 게시글 작성 시 태그를 지정할 수 있습니다.
- **익명**: 게시판별로 익명 작성이 가능합니다 (현재는 모든 게시판에서 익명 기능 미활용).

---

## 🚀 클라이언트 구현 가이드

### 게시판별 API 호출
```javascript
const boardApis = {
  community: '/api/posts/community',
  career: '/api/posts/career',
  love: '/api/posts/love',
  incident: '/api/posts/incident',
  vacation: '/api/posts/vacation'
};

// 커뮤니티 탭 게시판 조회
async function getCommunityPosts(page = 0, size = 20) {
  const response = await fetch(`${boardApis.community}?page=${page}&size=${size}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return response.json();
}

// 진로 상담 게시판에 게시글 작성
async function createCareerPost(title, content) {
  const response = await fetch('/api/posts', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      title,
      content,
      categoryId: 9 // 진로 상담
    })
  });
  return response.json();
}

// 영문 카테고리명으로 게시판 조회
const englishCategoryApis = {
  community: '/api/posts/category/english/community',
  career: '/api/posts/category/english/career',
  love: '/api/posts/category/english/love',
  incident: '/api/posts/category/english/incident',
  vacation: '/api/posts/category/english/vacation'
};

// 영문 카테고리명으로 게시글 조회
async function getPostsByEnglishCategory(englishName, page = 0, size = 20) {
  const response = await fetch(`${englishCategoryApis[englishName]}?page=${page}&size=${size}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return response.json();
}
``` 