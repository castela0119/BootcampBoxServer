# 댓글/대댓글 CRUD 및 신고 API 명세서

## 📋 개요

댓글과 대댓글의 생성, 조회, 수정, 삭제, 신고 기능을 제공하는 API 명세서입니다. 대댓글은 부모 댓글에 대한 답글 형태로 구현되어 있습니다.

## 🔗 API 엔드포인트 목록

### 1. 댓글 CRUD
- **POST** `/api/posts/{postId}/comments` - 댓글 작성
- **GET** `/api/posts/{postId}/comments` - 댓글 목록 조회 (페이징)
- **PATCH** `/api/posts/comments/{commentId}` - 댓글 수정
- **DELETE** `/api/posts/comments/{commentId}` - 댓글 삭제

### 2. 댓글 액션
- **POST** `/api/posts/comments/{commentId}/toggle-like` - 댓글 좋아요 토글
- **POST** `/api/posts/comments/{commentId}/report` - 댓글 신고

### 3. 관리자 전용
- **GET** `/api/posts/comments/{commentId}/likes` - 댓글 좋아요 사용자 목록
- **GET** `/api/posts/comments/{commentId}/reports` - 댓글 신고 사용자 목록
- **DELETE** `/api/posts/comments/{commentId}/report/{userId}` - 댓글 신고 취소

### 4. 내 댓글 관리
- **GET** `/api/posts/user/me/comments` - 내 댓글 목록 조회

---

## 📝 댓글 CRUD API 상세

### 1. 댓글 작성

#### 엔드포인트
```
POST /api/posts/{postId}/comments
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `postId` | Long | ✅ | 게시글 ID (Path Variable) |
| `content` | String | ✅ | 댓글 내용 |
| `parentId` | Long | ❌ | 부모 댓글 ID (대댓글인 경우) |

#### 요청 예시
```bash
# 일반 댓글 작성
curl -X POST "http://localhost:8080/api/posts/46/comments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "content": "좋은 게시글이네요!"
  }'

# 대댓글 작성
curl -X POST "http://localhost:8080/api/posts/46/comments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "content": "동감합니다!",
    "parentId": 12
  }'
```

#### 응답 예시
```json
{
  "id": 32,
  "content": "좋은 게시글이네요!",
  "authorNickname": "관리자2",
  "authorUsername": "admin2@gmail.com",
  "authorId": 14,
  "postId": 46,
  "parentId": null,
  "createdAt": "2025-07-19T13:30:00",
  "updatedAt": null,
  "replies": [],
  "likeCount": 0,
  "liked": false,
  "author": true
}
```

### 2. 댓글 수정

#### 엔드포인트
```
PATCH /api/posts/comments/{commentId}
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `commentId` | Long | ✅ | 댓글 ID (Path Variable) |
| `content` | String | ✅ | 수정할 댓글 내용 |

#### 요청 예시
```bash
curl -X PATCH "http://localhost:8080/api/posts/comments/32" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "content": "수정된 댓글 내용입니다."
  }'
```

#### 응답 예시
```json
{
  "id": 32,
  "content": "수정된 댓글 내용입니다.",
  "authorNickname": "관리자2",
  "authorUsername": "admin2@gmail.com",
  "authorId": 14,
  "postId": 46,
  "parentId": null,
  "createdAt": "2025-07-19T13:30:00",
  "updatedAt": "2025-07-19T13:35:00",
  "replies": [],
  "likeCount": 0,
  "liked": false,
  "author": true
}
```

### 3. 댓글 삭제

#### 엔드포인트
```
DELETE /api/posts/comments/{commentId}
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `commentId` | Long | ✅ | 댓글 ID (Path Variable) |

#### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/posts/comments/32" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 응답 예시
```json
{
  "message": "댓글이 성공적으로 삭제되었습니다.",
  "success": true
}
```

#### 삭제 제한사항
- **대댓글이 있는 댓글은 삭제할 수 없습니다.**
- **작성자만 삭제할 수 있습니다.**

---

## 🎯 댓글 액션 API 상세

### 1. 댓글 좋아요 토글

#### 엔드포인트
```
POST /api/posts/comments/{commentId}/toggle-like
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `commentId` | Long | ✅ | 댓글 ID (Path Variable) |

#### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/posts/comments/32/toggle-like" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 응답 예시
```json
{
  "message": "댓글에 좋아요를 눌렀습니다.",
  "count": 1,
  "success": true
}
```

### 2. 댓글 신고

#### 엔드포인트
```
POST /api/posts/comments/{commentId}/report
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `commentId` | Long | ✅ | 댓글 ID (Path Variable) |

#### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/posts/comments/32/report" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 응답 예시
```json
{
  "message": "댓글을 신고했습니다.",
  "count": 1,
  "success": true
}
```

#### 신고 제한사항
- **같은 댓글을 중복 신고할 수 없습니다.**
- **자신이 작성한 댓글은 신고할 수 없습니다.**

---

## 👨‍💼 관리자 전용 API 상세

### 1. 댓글 좋아요 사용자 목록

#### 엔드포인트
```
GET /api/posts/comments/{commentId}/likes
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `commentId` | Long | ✅ | 댓글 ID (Path Variable) |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/comments/32/likes" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### 응답 예시
```json
{
  "users": [
    {
      "id": 14,
      "username": "admin2@gmail.com",
      "nickname": "관리자2"
    },
    {
      "id": 13,
      "username": "admin@gmail.com",
      "nickname": "SUPER_ADMIN"
    }
  ],
  "count": 2
}
```

### 2. 댓글 신고 사용자 목록

#### 엔드포인트
```
GET /api/posts/comments/{commentId}/reports
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `commentId` | Long | ✅ | 댓글 ID (Path Variable) |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/comments/32/reports" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### 응답 예시
```json
{
  "users": [
    {
      "id": 8,
      "username": "domo@gamil.com",
      "nickname": "관리자1"
    }
  ],
  "count": 1
}
```

### 3. 댓글 신고 취소

#### 엔드포인트
```
DELETE /api/posts/comments/{commentId}/report/{userId}
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `commentId` | Long | ✅ | 댓글 ID (Path Variable) |
| `userId` | Long | ✅ | 사용자 ID (Path Variable) |

#### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/posts/comments/32/report/8" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### 응답 예시
```json
{
  "message": "신고가 취소되었습니다.",
  "count": 0,
  "success": true
}
```

---

## 👤 내 댓글 관리 API 상세

### 내 댓글 목록 조회

#### 엔드포인트
```
GET /api/posts/user/me/comments
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| `page` | Integer | ❌ | 0 | 페이지 번호 (0부터 시작) |
| `size` | Integer | ❌ | 10 | 페이지당 댓글 수 |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/user/me/comments?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 응답 예시
```json
{
  "content": [
    {
      "id": 32,
      "content": "좋은 게시글이네요!",
      "authorNickname": "관리자2",
      "authorUsername": "admin2@gmail.com",
      "authorId": 14,
      "postId": 46,
      "parentId": null,
      "createdAt": "2025-07-19T13:30:00",
      "updatedAt": null,
      "replies": [],
      "likeCount": 1,
      "liked": false,
      "author": true
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "first": true,
  "last": true,
  "numberOfElements": 1
}
```

---

## 🚨 에러 응답

### 400 Bad Request
```json
{
  "message": "댓글 작성 실패: 게시글을 찾을 수 없습니다.",
  "success": false
}
```

### 401 Unauthorized
```json
{
  "message": "인증이 필요합니다.",
  "success": false
}
```

### 403 Forbidden
```json
{
  "message": "댓글을 수정할 권한이 없습니다.",
  "success": false
}
```

### 404 Not Found
```json
{
  "message": "댓글을 찾을 수 없습니다.",
  "success": false
}
```

---

## 🔧 클라이언트 구현 가이드

### Flutter/Dart 구현 예시

#### 댓글 작성
```dart
Future<CommentResponse> createComment(int postId, String content, {int? parentId}) async {
  final response = await dio.post(
    '/api/posts/$postId/comments',
    data: {
      'content': content,
      if (parentId != null) 'parentId': parentId,
    },
  );
  
  return CommentResponse.fromJson(response.data);
}
```

#### 댓글 수정
```dart
Future<CommentResponse> updateComment(int commentId, String content) async {
  final response = await dio.patch(
    '/api/posts/comments/$commentId',
    data: {'content': content},
  );
  
  return CommentResponse.fromJson(response.data);
}
```

#### 댓글 삭제
```dart
Future<void> deleteComment(int commentId) async {
  await dio.delete('/api/posts/comments/$commentId');
}
```

#### 댓글 좋아요 토글
```dart
Future<ActionResponse> toggleCommentLike(int commentId) async {
  final response = await dio.post(
    '/api/posts/comments/$commentId/toggle-like',
  );
  
  return ActionResponse.fromJson(response.data);
}
```

#### 댓글 신고
```dart
Future<ActionResponse> reportComment(int commentId) async {
  final response = await dio.post(
    '/api/posts/comments/$commentId/report',
  );
  
  return ActionResponse.fromJson(response.data);
}
```

---

## ⚠️ 주의사항

### 1. 권한 관리
- **댓글 수정/삭제**: 작성자만 가능
- **댓글 신고**: 로그인한 사용자만 가능 (자신의 댓글 제외)
- **관리자 기능**: ADMIN 역할 필요

### 2. 대댓글 제한사항
- **대댓글 작성**: 부모 댓글이 같은 게시글에 있어야 함
- **댓글 삭제**: 대댓글이 있으면 삭제 불가
- **대댓글 깊이**: 1단계만 지원 (대대댓글 없음)

### 3. 신고 시스템
- **중복 신고 방지**: 같은 사용자가 같은 댓글을 중복 신고 불가
- **신고 취소**: 관리자만 가능
- **신고 수 집계**: 댓글의 신고 수는 실시간 업데이트

### 4. 좋아요 시스템
- **토글 방식**: 한 번 호출하면 좋아요 추가, 다시 호출하면 취소
- **중복 방지**: 같은 사용자가 같은 댓글에 중복 좋아요 불가

---

## 📊 성능 고려사항

1. **대댓글 로딩**: 각 댓글의 대댓글은 별도 쿼리로 로딩
2. **좋아요/신고 상태**: 현재 사용자의 상태는 별도 확인
3. **페이징**: 댓글 목록은 페이징으로 처리
4. **캐싱**: 자주 조회되는 댓글 정보는 캐싱 고려

---

**이 명세서를 참고하여 댓글/대댓글 기능을 구현하면 됩니다!** 🚀 