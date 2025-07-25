# 🚨 클라이언트 API 응답 형식 변경 안내서

## 📋 변경 개요

서버 리팩토링으로 인해 **모든 API 응답 형식이 변경**되었습니다. 클라이언트 코드 수정이 필요합니다.

**변경 일자**: 2025-07-23  
**영향 범위**: 모든 API 엔드포인트

---

## 🔄 응답 형식 변경 사항

### 기존 응답 형식 (리팩토링 전)
```json
{
  "content": [...],
  "pageable": {...},
  "totalElements": 47,
  "totalPages": 5,
  "first": true,
  "last": false,
  "numberOfElements": 10
}
```

### 새로운 응답 형식 (리팩토링 후)
```json
{
  "success": true,
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {
    "content": [...],
    "pageable": {...},
    "totalElements": 47,
    "totalPages": 5,
    "first": true,
    "last": false,
    "numberOfElements": 10
  },
  "error": null,
  "timestamp": "2025-07-23T21:54:46.100811"
}
```

---

## 🛠️ 클라이언트 수정 방법

### 1. 게시글 관련 API

#### 게시글 목록 조회
```javascript
// 기존 코드
const response = await fetch('/api/posts?page=0&size=10');
const data = await response.json();
const posts = data.content; // ❌ 이제 작동하지 않음

// 수정된 코드
const response = await fetch('/api/posts?page=0&size=10');
const data = await response.json();
const posts = data.data.content; // ✅ 새로운 구조
```

#### 커뮤니티 게시글 조회
```javascript
// 기존 코드
const response = await fetch('/api/posts/community');
const data = await response.json();
const posts = data.content; // ❌ 이제 작동하지 않음

// 수정된 코드
const response = await fetch('/api/posts/community');
const data = await response.json();
const posts = data.data.content; // ✅ 새로운 구조
```

#### 게시글 상세 조회
```javascript
// 기존 코드
const response = await fetch(`/api/posts/${postId}`);
const post = await response.json();

// 수정된 코드
const response = await fetch(`/api/posts/${postId}`);
const data = await response.json();
const post = data.data; // ✅ 새로운 구조
```

### 2. 댓글 관련 API

#### 댓글 목록 조회
```javascript
// 기존 코드
const response = await fetch(`/api/posts/${postId}/comments`);
const data = await response.json();
const comments = data.content;

// 수정된 코드
const response = await fetch(`/api/posts/${postId}/comments`);
const data = await response.json();
const comments = data.data.content; // ✅ 새로운 구조
```

#### 댓글 작성/수정/삭제
```javascript
// 기존 코드
const response = await fetch('/api/comments', {
  method: 'POST',
  body: JSON.stringify(commentData)
});
const result = await response.json();

// 수정된 코드
const response = await fetch('/api/comments', {
  method: 'POST',
  body: JSON.stringify(commentData)
});
const data = await response.json();
const result = data.data; // ✅ 새로운 구조
```

### 3. 페이지네이션 정보 접근

```javascript
// 기존 코드
const totalElements = data.totalElements;
const totalPages = data.totalPages;
const isFirst = data.first;
const isLast = data.last;
const currentPage = data.number;

// 수정된 코드
const totalElements = data.data.totalElements;
const totalPages = data.data.totalPages;
const isFirst = data.data.first;
const isLast = data.data.last;
const currentPage = data.data.number;
```

### 4. 에러 처리 개선

```javascript
// 기존 코드
if (!response.ok) {
  throw new Error('API 호출 실패');
}

// 수정된 코드
const data = await response.json();
if (!data.success) {
  throw new Error(data.message || 'API 호출 실패');
}
```

---

## 📝 수정이 필요한 모든 API 엔드포인트

### 게시글 관련
- [ ] `GET /api/posts` - 게시글 목록
- [ ] `GET /api/posts/{id}` - 게시글 상세
- [ ] `GET /api/posts/community` - 커뮤니티 게시글
- [ ] `GET /api/posts/hot` - 인기 게시글
- [ ] `POST /api/posts` - 게시글 작성
- [ ] `PUT /api/posts/{id}` - 게시글 수정
- [ ] `DELETE /api/posts/{id}` - 게시글 삭제

### 댓글 관련
- [ ] `GET /api/posts/{postId}/comments` - 댓글 목록
- [ ] `GET /api/comments/{id}` - 댓글 상세
- [ ] `POST /api/comments` - 댓글 작성
- [ ] `PUT /api/comments/{id}` - 댓글 수정
- [ ] `DELETE /api/comments/{id}` - 댓글 삭제

### 사용자 관련
- [ ] `GET /api/user/me` - 내 정보 조회
- [ ] `PUT /api/user/me` - 내 정보 수정
- [ ] `GET /api/user/{id}` - 사용자 정보 조회

### 인증 관련
- [ ] `POST /api/auth/login` - 로그인
- [ ] `POST /api/auth/signup` - 회원가입
- [ ] `POST /api/auth/logout` - 로그아웃

### 기타
- [ ] `GET /api/categories` - 카테고리 목록
- [ ] `GET /api/tags` - 태그 목록
- [ ] `GET /api/notifications` - 알림 목록

---

## 🔧 유틸리티 함수 제안

### 1. API 응답 파싱 헬퍼 함수

```javascript
// apiHelper.js
export const parseApiResponse = async (response) => {
  const data = await response.json();
  
  if (!data.success) {
    throw new Error(data.message || 'API 호출 실패');
  }
  
  return data.data;
};

// 사용 예시
const posts = await parseApiResponse(
  await fetch('/api/posts')
);
```

### 2. 페이지네이션 데이터 추출 함수

```javascript
// paginationHelper.js
export const extractPaginationData = (data) => {
  return {
    content: data.data.content,
    totalElements: data.data.totalElements,
    totalPages: data.data.totalPages,
    currentPage: data.data.number,
    isFirst: data.data.first,
    isLast: data.data.last,
    hasNext: !data.data.last,
    hasPrevious: !data.data.first
  };
};

// 사용 예시
const response = await fetch('/api/posts');
const data = await response.json();
const { content, totalElements, hasNext } = extractPaginationData(data);
```

---

## ⚠️ 주의사항

### 1. 성공/실패 판단
```javascript
// ❌ 잘못된 방법
if (response.ok) {
  // 성공 처리
}

// ✅ 올바른 방법
const data = await response.json();
if (data.success) {
  // 성공 처리
} else {
  // 실패 처리
  console.error(data.message);
}
```

### 2. 데이터 접근
```javascript
// ❌ 잘못된 방법
const posts = data.content;

// ✅ 올바른 방법
const posts = data.data.content;
```

### 3. 에러 메시지
```javascript
// ❌ 잘못된 방법
throw new Error('API 호출 실패');

// ✅ 올바른 방법
throw new Error(data.message || 'API 호출 실패');
```

---

## 🧪 테스트 방법

### 1. API 응답 확인
```bash
# 게시글 목록 API 테스트
curl -X GET "http://localhost:8080/api/posts?page=0&size=5" \
  -H "Content-Type: application/json"

# 커뮤니티 게시글 API 테스트
curl -X GET "http://localhost:8080/api/posts/community" \
  -H "Content-Type: application/json"
```

### 2. 응답 구조 확인
```javascript
// 브라우저 개발자 도구에서
fetch('/api/posts')
  .then(response => response.json())
  .then(data => {
    console.log('전체 응답:', data);
    console.log('실제 데이터:', data.data);
    console.log('성공 여부:', data.success);
    console.log('메시지:', data.message);
  });
```

---

## 📞 문의사항

수정 과정에서 문제가 발생하거나 궁금한 점이 있으시면:

1. **서버 로그 확인**: `logs/application.log`
2. **API 응답 확인**: 브라우저 개발자 도구 Network 탭
3. **문의**: 개발팀에 연락

---

## ✅ 체크리스트

- [ ] 모든 API 호출 코드에서 `data.data` 구조로 변경
- [ ] 페이지네이션 정보 접근 방식 수정
- [ ] 에러 처리 로직 개선
- [ ] 성공/실패 판단 로직 수정
- [ ] 테스트 완료
- [ ] 배포 전 최종 확인

**마감일**: 가능한 한 빨리 수정하여 커뮤니티 탭 기능 복구 필요 