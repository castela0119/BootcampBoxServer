# 게시글 뮤트 API 명세서

## 📋 개요
게시글 뮤트 기능을 통해 사용자는 특정 게시글의 알림을 차단할 수 있습니다. 뮤트된 게시글에서는 댓글 알림이 전송되지 않습니다.

## 🔗 기본 정보
- **Base URL**: `/api/posts/mute`
- **인증**: JWT 토큰 필요 (Authorization 헤더)
- **Content-Type**: `application/json`

## 🔐 인증 처리 방식
- JWT 토큰에서 사용자 email 추출
- email로 사용자 조회 후 userId 획득
- 이중 검증: JWT 토큰 검증 + DB 사용자 존재 확인

## 📝 API 엔드포인트

### 1. 게시글 뮤트
**POST** `/api/posts/mute/{postId}`

게시글을 뮤트하여 해당 게시글의 알림을 차단합니다.

#### 요청
```http
POST /api/posts/mute/48
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### 응답
**성공 (200 OK)**
```json
{
  "message": "게시글이 뮤트되었습니다.",
  "success": true,
  "data": {
    "userId": 1,
    "postId": 48,
    "mutedAt": "2025-01-15T10:30:00",
    "message": "게시글이 성공적으로 뮤트되었습니다."
  }
}
```

**이미 뮤트됨 (409 Conflict)**
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

**게시글 없음 (404 Not Found)**
```http
HTTP/1.1 404 Not Found
```

**인증 실패 (401 Unauthorized)**
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

**사용자 없음 (401 Unauthorized)**
```json
{
  "message": "인증이 필요합니다.",
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "details": "사용자를 찾을 수 없습니다."
  }
}
```

---

### 2. 게시글 언뮤트
**DELETE** `/api/posts/mute/{postId}`

게시글 뮤트를 해제하여 알림을 다시 받을 수 있도록 합니다.

#### 요청
```http
DELETE /api/posts/mute/48
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### 응답
**성공 (200 OK)**
```json
{
  "message": "게시글 뮤트가 해제되었습니다.",
  "success": true,
  "data": {
    "userId": 1,
    "postId": 48,
    "unmutedAt": "2025-01-15T11:00:00",
    "message": "게시글 뮤트가 성공적으로 해제되었습니다."
  }
}
```

**게시글 없음 (404 Not Found)**
```http
HTTP/1.1 404 Not Found
```

**인증 실패 (401 Unauthorized)**
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

**사용자 없음 (401 Unauthorized)**
```json
{
  "message": "인증이 필요합니다.",
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "details": "사용자를 찾을 수 없습니다."
  }
}
```

---

### 3. 게시글 뮤트 상태 조회
**GET** `/api/posts/mute/{postId}`

특정 게시글의 뮤트 상태를 확인합니다.

#### 요청
```http
GET /api/posts/mute/48
Authorization: Bearer <JWT_TOKEN>
```

#### 응답
**성공 (200 OK)**
```json
{
  "message": "뮤트 상태를 성공적으로 조회했습니다.",
  "success": true,
  "data": {
    "userId": 1,
    "postId": 48,
    "isMuted": true,
    "mutedAt": "2025-01-15T10:30:00",
    "postTitle": "테스트 게시글 제목"
  }
}
```

**뮤트되지 않음**
```json
{
  "message": "뮤트 상태를 성공적으로 조회했습니다.",
  "success": true,
  "data": {
    "userId": 1,
    "postId": 48,
    "isMuted": false,
    "mutedAt": null,
    "postTitle": "테스트 게시글 제목"
  }
}
```

**게시글 없음 (404 Not Found)**
```http
HTTP/1.1 404 Not Found
```

**인증 실패 (401 Unauthorized)**
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

**사용자 없음 (401 Unauthorized)**
```json
{
  "message": "인증이 필요합니다.",
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "details": "사용자를 찾을 수 없습니다."
  }
}
```

---

### 4. 뮤트된 게시글 목록 조회
**GET** `/api/posts/mute/list`

사용자가 뮤트한 게시글 목록을 페이지네이션과 함께 조회합니다.

#### 요청
```http
GET /api/posts/mute/list?page=0&size=20
Authorization: Bearer <JWT_TOKEN>
```

#### 쿼리 파라미터
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 20)

#### 응답
**성공 (200 OK)**
```json
{
  "message": "뮤트된 게시글 목록을 성공적으로 조회했습니다.",
  "success": true,
  "data": {
    "mutedPosts": [
      {
        "postId": 48,
        "postTitle": "테스트 게시글 1",
        "authorNickname": "작성자1",
        "mutedAt": "2025-01-15T10:30:00"
      },
      {
        "postId": 52,
        "postTitle": "테스트 게시글 2",
        "authorNickname": "작성자2",
        "mutedAt": "2025-01-14T15:20:00"
      }
    ],
    "pagination": {
      "currentPage": 0,
      "totalPages": 1,
      "totalElements": 2,
      "pageSize": 20,
      "hasNext": false,
      "hasPrevious": false
    }
  }
}
```

**뮤트된 게시글 없음**
```json
{
  "message": "뮤트된 게시글 목록을 성공적으로 조회했습니다.",
  "success": true,
  "data": {
    "mutedPosts": [],
    "pagination": {
      "currentPage": 0,
      "totalPages": 0,
      "totalElements": 0,
      "pageSize": 20,
      "hasNext": false,
      "hasPrevious": false
    }
  }
}
```

**인증 실패 (401 Unauthorized)**
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

**사용자 없음 (401 Unauthorized)**
```json
{
  "message": "인증이 필요합니다.",
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "details": "사용자를 찾을 수 없습니다."
  }
}
```

---

## 🔧 클라이언트 구현 예시

### JavaScript (Fetch API)
```javascript
// 게시글 뮤트
async function mutePost(postId, token) {
  try {
    const response = await fetch(`/api/posts/mute/${postId}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (response.ok) {
      console.log('게시글 뮤트 성공:', result.message);
      return result.data;
    } else {
      console.error('게시글 뮤트 실패:', result.message);
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('네트워크 오류:', error);
    throw error;
  }
}

// 게시글 언뮤트
async function unmutePost(postId, token) {
  try {
    const response = await fetch(`/api/posts/mute/${postId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (response.ok) {
      console.log('게시글 언뮤트 성공:', result.message);
      return result.data;
    } else {
      console.error('게시글 언뮤트 실패:', result.message);
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('네트워크 오류:', error);
    throw error;
  }
}

// 뮤트 상태 조회
async function getMuteStatus(postId, token) {
  try {
    const response = await fetch(`/api/posts/mute/${postId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    const result = await response.json();
    
    if (response.ok) {
      return result.data.isMuted;
    } else {
      console.error('뮤트 상태 조회 실패:', result.message);
      return false;
    }
  } catch (error) {
    console.error('네트워크 오류:', error);
    return false;
  }
}

// 뮤트된 게시글 목록 조회
async function getMutedPosts(page = 0, size = 20, token) {
  try {
    const response = await fetch(`/api/posts/mute/list?page=${page}&size=${size}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    const result = await response.json();
    
    if (response.ok) {
      return result.data;
    } else {
      console.error('뮤트된 게시글 목록 조회 실패:', result.message);
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('네트워크 오류:', error);
    throw error;
  }
}
```

### cURL 예시
```bash
# 게시글 뮤트
curl -X POST http://localhost:8080/api/posts/mute/48 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"

# 게시글 언뮤트
curl -X DELETE http://localhost:8080/api/posts/mute/48 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"

# 뮤트 상태 조회
curl -X GET http://localhost:8080/api/posts/mute/48 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 뮤트된 게시글 목록 조회
curl -X GET "http://localhost:8080/api/posts/mute/list?page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## ⚠️ 에러 코드

| HTTP 상태 코드 | 에러 코드 | 설명 |
|---------------|-----------|------|
| 401 | UNAUTHORIZED | 인증이 필요하거나 토큰이 유효하지 않음 |
| 401 | UNAUTHORIZED | 사용자를 찾을 수 없음 (DB에서 조회 실패) |
| 404 | NOT_FOUND | 게시글이 존재하지 않음 |
| 409 | ALREADY_MUTED | 이미 뮤트된 게시글 |
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |

---

## 📋 비즈니스 로직

### 뮤트 기능 동작 방식
1. **뮤트 설정**: 사용자가 특정 게시글을 뮤트하면 해당 게시글의 댓글 알림이 차단됩니다.
2. **알림 차단**: 뮤트된 게시글에 댓글이 달려도 알림이 전송되지 않습니다.
3. **뮤트 해제**: 언뮤트하면 다시 알림을 받을 수 있습니다.
4. **상태 유지**: 뮤트 상태는 데이터베이스에 영구 저장됩니다.

### 인증 처리 방식
1. **JWT 토큰 검증**: 토큰의 서명과 만료시간 확인
2. **사용자 조회**: JWT에서 추출한 email로 DB에서 사용자 조회
3. **이중 검증**: 토큰 유효성 + 사용자 존재 여부 확인
4. **권한 확인**: 사용자의 권한 정보 검증

### 제한사항
- 한 사용자가 동일한 게시글을 중복 뮤트할 수 없습니다.
- 존재하지 않는 게시글은 뮤트할 수 없습니다.
- 인증된 사용자만 뮤트 기능을 사용할 수 있습니다.
- JWT 토큰이 유효하고 DB에 사용자가 존재해야 합니다.

---

## 🔄 알림 시스템 연동

뮤트 기능은 알림 시스템과 연동되어 작동합니다:

```java
// NotificationService에서 뮤트 체크
public void sendCommentNotification(Long userId, Long postId, String commentContent) {
    // 뮤트 상태 확인
    if (postMuteService.isPostMutedByUser(userId, postId)) {
        log.info("뮤트된 게시글이므로 알림 전송 건너뜀: userId={}, postId={}", userId, postId);
        return;
    }
    
    // 알림 전송 로직
    // ...
}
```

이를 통해 뮤트된 게시글의 댓글 알림이 자동으로 차단됩니다.

---

## 🏗️ 아키텍처 개요

### 컨트롤러 분리
- **PostController**: 게시글 CRUD 기능
- **PostMuteController**: 게시글 뮤트 관련 기능 (별도 컨트롤러)

### 경로 구조
- `/api/posts/**`: 게시글 기본 기능
- `/api/posts/mute/**`: 게시글 뮤트 기능

### 보안 강화
- JWT 토큰 기반 인증
- 이중 검증 (토큰 + DB 사용자 조회)
- 경로별 인증 요구사항 설정 