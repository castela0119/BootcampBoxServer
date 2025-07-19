# 댓글 페이징 API 명세서

## 📋 개요

댓글 페이징 API는 게시글의 댓글을 페이지 단위로 조회하는 API입니다. 무한 스크롤 방식으로 구현되어 있으며, 대댓글 기능을 지원합니다.

## 🔗 API 엔드포인트

### 댓글 목록 조회
```
GET /api/posts/{postId}/comments
```

## 📝 요청 파라미터

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| `postId` | Long | ✅ | - | 게시글 ID (Path Variable) |
| `page` | Integer | ❌ | 0 | 페이지 번호 (0부터 시작) |
| `size` | Integer | ❌ | 10 | 페이지당 댓글 수 |

### 요청 예시
```bash
# 첫 번째 페이지 (0-9번 댓글)
curl -X GET "http://localhost:8080/api/posts/46/comments?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 두 번째 페이지 (10-19번 댓글)
curl -X GET "http://localhost:8080/api/posts/46/comments?page=1&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 마지막 페이지
curl -X GET "http://localhost:8080/api/posts/46/comments?page=3&size=5" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 📤 응답 구조

### 성공 응답 (200 OK)

```json
{
  "comments": [
    {
      "id": 12,
      "content": "테스트 댓글 1 (게시글 46)",
      "authorNickname": "SUPER_ADMIN",
      "authorUsername": "admin@gmail.com",
      "authorId": 13,
      "postId": 46,
      "parentId": null,
      "createdAt": "2025-07-18T07:18:19",
      "updatedAt": null,
      "replies": [],
      "likeCount": 0,
      "liked": false,
      "author": false
    }
  ],
  "totalComments": 20,
  "currentPage": 0,
  "totalPages": 4,
  "hasNext": true,
  "hasPrevious": false
}
```

### 댓글이 없는 경우

```json
{
  "comments": [],
  "totalComments": 0,
  "currentPage": 0,
  "totalPages": 0,
  "hasNext": false,
  "hasPrevious": false
}
```

## 📊 응답 필드 상세

### CommentListResponse
| 필드 | 타입 | 설명 |
|------|------|------|
| `comments` | Array | 댓글 목록 |
| `totalComments` | Long | 전체 댓글 수 |
| `currentPage` | Integer | 현재 페이지 번호 (0부터 시작) |
| `totalPages` | Integer | 전체 페이지 수 |
| `hasNext` | Boolean | 다음 페이지 존재 여부 |
| `hasPrevious` | Boolean | 이전 페이지 존재 여부 |

### CommentResponse
| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 댓글 ID |
| `content` | String | 댓글 내용 |
| `authorNickname` | String | 작성자 닉네임 |
| `authorUsername` | String | 작성자 이메일 |
| `authorId` | Long | 작성자 ID |
| `postId` | Long | 게시글 ID |
| `parentId` | Long | 부모 댓글 ID (대댓글인 경우) |
| `createdAt` | String | 작성일시 (ISO 8601) |
| `updatedAt` | String | 수정일시 (ISO 8601, null 가능) |
| `replies` | Array | 대댓글 목록 |
| `likeCount` | Integer | 좋아요 수 |
| `liked` | Boolean | 현재 사용자의 좋아요 여부 |
| `author` | Boolean | 현재 사용자가 작성자인지 여부 |

## 🔍 실제 응답 예시

### 페이지 0 (첫 번째 페이지)
```json
{
  "comments": [
    {
      "id": 12,
      "content": "테스트 댓글 1 (게시글 46)",
      "authorNickname": "SUPER_ADMIN",
      "authorUsername": "admin@gmail.com",
      "authorId": 13,
      "postId": 46,
      "parentId": null,
      "createdAt": "2025-07-18T07:18:19",
      "updatedAt": null,
      "replies": [],
      "likeCount": 0,
      "liked": false,
      "author": false
    },
    {
      "id": 13,
      "content": "테스트 댓글 2 (게시글 46)",
      "authorNickname": "관리자2",
      "authorUsername": "admin2@gmail.com",
      "authorId": 14,
      "postId": 46,
      "parentId": null,
      "createdAt": "2025-07-18T07:18:19",
      "updatedAt": null,
      "replies": [],
      "likeCount": 0,
      "liked": false,
      "author": false
    }
  ],
  "totalComments": 20,
  "currentPage": 0,
  "totalPages": 4,
  "hasNext": true,
  "hasPrevious": false
}
```

### 페이지 1 (두 번째 페이지)
```json
{
  "comments": [
    {
      "id": 17,
      "content": "테스트 댓글 6 (게시글 46)",
      "authorNickname": "SUPER_ADMIN",
      "authorUsername": "admin@gmail.com",
      "authorId": 13,
      "postId": 46,
      "parentId": null,
      "createdAt": "2025-07-18T07:18:19",
      "updatedAt": null,
      "replies": [],
      "likeCount": 0,
      "liked": false,
      "author": false
    }
  ],
  "totalComments": 20,
  "currentPage": 1,
  "totalPages": 4,
  "hasNext": true,
  "hasPrevious": true
}
```

### 마지막 페이지 (페이지 3)
```json
{
  "comments": [
    {
      "id": 27,
      "content": "테스트 댓글 16 (게시글 46)",
      "authorNickname": "SUPER_ADMIN",
      "authorUsername": "admin@gmail.com",
      "authorId": 13,
      "postId": 46,
      "parentId": null,
      "createdAt": "2025-07-18T07:18:19",
      "updatedAt": null,
      "replies": [],
      "likeCount": 0,
      "liked": false,
      "author": false
    },
    {
      "id": 31,
      "content": "테스트 댓글 20 (게시글 46)",
      "authorNickname": "domo",
      "authorUsername": "",
      "authorId": 1,
      "postId": 46,
      "parentId": null,
      "createdAt": "2025-07-18T07:18:19",
      "updatedAt": null,
      "replies": [],
      "likeCount": 0,
      "liked": false,
      "author": false
    }
  ],
  "totalComments": 20,
  "currentPage": 3,
  "totalPages": 4,
  "hasNext": false,
  "hasPrevious": true
}
```

## 🚨 에러 응답

### 400 Bad Request
```json
{
  "message": "댓글 목록 조회 실패: 게시글을 찾을 수 없습니다.",
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

## 🔧 클라이언트 구현 가이드

### Flutter/Dart 구현 예시

```dart
class CommentListState {
  List<Comment> comments = [];
  int currentPage = 0;
  bool isLoading = false;
  bool hasNext = true;
  
  void reset() {
    comments.clear();
    currentPage = 0;
    isLoading = false;
    hasNext = true;
  }
}

Future<void> loadComments(int postId, {bool refresh = false}) async {
  if (refresh) {
    state.reset();
  }
  
  if (state.isLoading || !state.hasNext) {
    return;
  }
  
  try {
    state.isLoading = true;
    
    final response = await dio.get(
      '/api/posts/$postId/comments',
      queryParameters: {
        'page': state.currentPage,
        'size': 10,
      },
    );
    
    final commentListResponse = CommentListResponse.fromJson(response.data);
    
    if (refresh) {
      state.comments = commentListResponse.comments;
    } else {
      state.comments.addAll(commentListResponse.comments);
    }
    
    // 중요: 서버 응답의 hasNext 값을 사용
    state.hasNext = commentListResponse.hasNext;
    
    // 중요: 페이지 번호 증가 로직 수정
    if (refresh) {
      state.currentPage = 1; // 새로고침 후 다음 페이지는 1
    } else {
      state.currentPage = commentListResponse.currentPage + 1; // 현재 페이지 + 1
    }
    
  } catch (e) {
    print('댓글 로딩 오류: $e');
  } finally {
    state.isLoading = false;
  }
}
```

### 무한 스크롤 구현

```dart
ListView.builder(
  itemCount: state.comments.length + (state.hasNext ? 1 : 0),
  itemBuilder: (context, index) {
    // 마지막 아이템이고 더 로드할 데이터가 있으면 로딩 표시
    if (index == state.comments.length && state.hasNext) {
      return const Center(
        child: Padding(
          padding: EdgeInsets.all(16.0),
          child: CircularProgressIndicator(),
        ),
      );
    }
    
    // 실제 댓글 아이템
    final comment = state.comments[index];
    return CommentWidget(comment: comment);
  },
)
```

## ⚠️ 주의사항

### 1. 페이지 번호 증가 로직
- **잘못된 방법**: `state.currentPage = commentListResponse.currentPage + 1`
- **올바른 방법**: 
  ```dart
  if (refresh) {
    state.currentPage = 1; // 새로고침 후 다음 페이지는 1
  } else {
    state.currentPage = commentListResponse.currentPage + 1; // 현재 페이지 + 1
  }
  ```

### 2. hasNext 값 사용
- 서버 응답의 `hasNext` 값을 그대로 사용
- 클라이언트에서 임의로 계산하지 않음

### 3. 초기 로딩
- 초기 로딩 시 `refresh=false` 사용
- 새 댓글 작성 후에만 `refresh=true` 사용

### 4. 대댓글 처리
- `replies` 배열에 대댓글 목록이 포함됨
- `parentId`가 null이면 일반 댓글, 값이 있으면 대댓글

## 🧪 테스트 시나리오

### 정상적인 페이징 동작
1. **초기 로딩**: `page=0` → 댓글 1~10번 (10개)
2. **스크롤 끝**: `page=1` → 댓글 11~20번 (10개)
3. **더 이상 없음**: `hasNext=false` → 로딩 중단

### 댓글이 없는 게시글
- `comments: []`
- `totalComments: 0`
- `hasNext: false`

### 마지막 페이지
- `hasNext: false`
- `hasPrevious: true`

## 📊 성능 고려사항

1. **페이지 크기**: 기본값 10개, 최대 50개 권장
2. **대댓글 로딩**: 각 댓글의 대댓글은 별도 쿼리로 로딩
3. **캐싱**: 클라이언트에서 이전 페이지 데이터 캐싱 고려
4. **무한 스크롤**: 스크롤 위치 기반으로 다음 페이지 자동 로딩

---

**이 명세서를 참고하여 클라이언트에서 댓글 페이징을 구현하면 정상적으로 작동할 것입니다!** 🚀 