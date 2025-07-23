# PostMuteController API 명세서

## 📋 개요
게시글 뮤트 기능을 제공하는 API 컨트롤러입니다. 사용자가 특정 게시글을 뮤트하거나 언뮤트할 수 있으며, 뮤트된 게시글 목록을 조회할 수 있습니다.

## 🔗 기본 경로
```
/api/posts
```

## 📚 API 엔드포인트

### 1. 뮤트된 게시글 목록 조회

**GET** `/api/posts/muted`

뮤트된 게시글 목록을 페이징으로 조회합니다.

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | int | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | int | ❌ | 20 | 페이지당 게시글 수 |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/muted?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 예시
```json
{
  "message": "뮤트된 게시글 목록을 성공적으로 조회했습니다.",
  "success": true,
  "data": {
    "mutedPosts": [
      {
        "postId": 123,
        "postTitle": "뮤트된 게시글 제목",
        "postAuthor": "작성자명",
        "mutedAt": "2024-01-15T10:30:00"
      }
    ],
    "pagination": {
      "currentPage": 0,
      "totalPages": 5,
      "totalElements": 100,
      "hasNext": true
    }
  }
}
```

#### 에러 응답
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

### 2. 게시글 뮤트 상태 조회

**GET** `/api/posts/{postId}/mute`

특정 게시글의 뮤트 상태를 조회합니다.

#### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| postId | Long | ✅ | 게시글 ID |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/123/mute" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 예시 (뮤트된 경우)
```json
{
  "message": "뮤트 상태를 성공적으로 조회했습니다.",
  "success": true,
  "data": {
    "isMuted": true,
    "mutedAt": "2024-01-15T10:30:00"
  }
}
```

#### 응답 예시 (뮤트되지 않은 경우)
```json
{
  "message": "뮤트 상태를 성공적으로 조회했습니다.",
  "success": true,
  "data": {
    "isMuted": false,
    "mutedAt": null
  }
}
```

---

### 3. 게시글 뮤트

**POST** `/api/posts/{postId}/mute`

특정 게시글을 뮤트합니다.

#### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| postId | Long | ✅ | 게시글 ID |

#### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/posts/123/mute" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 예시
```json
{
  "message": "게시글이 뮤트되었습니다.",
  "success": true,
  "data": {
    "postId": 123,
    "userId": 456,
    "mutedAt": "2024-01-15T10:30:00"
  }
}
```

#### 에러 응답 (이미 뮤트된 경우)
```json
{
  "message": "이미 뮤트된 게시글입니다.",
  "success": false,
  "error": {
    "code": "ALREADY_MUTED",
    "details": "이미 뮤트된 게시글입니다."
  }
}
```

---

### 4. 게시글 언뮤트

**DELETE** `/api/posts/{postId}/mute`

특정 게시글의 뮤트를 해제합니다.

#### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| postId | Long | ✅ | 게시글 ID |

#### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/posts/123/mute" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 예시
```json
{
  "message": "게시글 뮤트가 해제되었습니다.",
  "success": true,
  "data": {
    "postId": 123,
    "userId": 456,
    "unmutedAt": "2024-01-15T10:30:00"
  }
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
| 401 | 인증 실패 |
| 404 | 게시글 또는 사용자를 찾을 수 없음 |
| 409 | 이미 뮤트된 게시글 |
| 500 | 서버 내부 오류 |

---

## 🏗️ 데이터 모델

### PostMuteDto.MutedPostInfo
```json
{
  "postId": "Long",
  "postTitle": "String",
  "postAuthor": "String",
  "mutedAt": "LocalDateTime"
}
```

### PostMuteDto.MuteStatusResponse
```json
{
  "isMuted": "boolean",
  "mutedAt": "LocalDateTime (nullable)"
}
```

### PostMuteDto.MuteResponse
```json
{
  "postId": "Long",
  "userId": "Long",
  "mutedAt": "LocalDateTime"
}
```

### PostMuteDto.UnmuteResponse
```json
{
  "postId": "Long",
  "userId": "Long",
  "unmutedAt": "LocalDateTime"
}
```

### PostMuteDto.PaginationInfo
```json
{
  "currentPage": "int",
  "totalPages": "int",
  "totalElements": "long",
  "hasNext": "boolean"
}
```

---

## 🔄 비즈니스 로직

### 뮤트 기능
- 사용자는 자신이 작성한 게시글을 뮤트할 수 없습니다.
- 이미 뮤트된 게시글을 다시 뮤트하려고 하면 409 에러가 발생합니다.
- 뮤트된 게시글은 알림에서 제외됩니다.

### 언뮤트 기능
- 뮤트되지 않은 게시글을 언뮤트하려고 하면 404 에러가 발생합니다.
- 언뮤트 후에는 해당 게시글의 알림을 다시 받을 수 있습니다.

### 목록 조회
- 페이징을 지원하여 대량의 뮤트된 게시글을 효율적으로 조회할 수 있습니다.
- 삭제된 게시글의 경우 제목이 "[삭제된 게시글]"로 표시됩니다.

---

## 🧪 테스트 예시

### 전체 테스트 시나리오
```bash
# 1. 뮤트된 게시글 목록 조회
curl -X GET "http://localhost:8080/api/posts/muted" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 2. 게시글 뮤트 상태 조회 (뮤트되지 않은 상태)
curl -X GET "http://localhost:8080/api/posts/123/mute" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 3. 게시글 뮤트
curl -X POST "http://localhost:8080/api/posts/123/mute" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 4. 게시글 뮤트 상태 조회 (뮤트된 상태)
curl -X GET "http://localhost:8080/api/posts/123/mute" \
  -H "Authorization: Bearer {JWT_TOKEN}"

# 5. 게시글 언뮤트
curl -X DELETE "http://localhost:8080/api/posts/123/mute" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

---

## 📝 참고사항

- 모든 시간은 ISO 8601 형식 (YYYY-MM-DDTHH:mm:ss)으로 반환됩니다.
- 페이지 번호는 0부터 시작합니다.
- 게시글이 삭제된 경우에도 뮤트 정보는 유지됩니다.
- 뮤트된 게시글은 알림 서비스에서 자동으로 제외됩니다. 