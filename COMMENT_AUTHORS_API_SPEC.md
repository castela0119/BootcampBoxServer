# 댓글 작성자 조회 API 명세서

## 개요
특정 게시글에 댓글을 단 모든 사용자들의 정보를 조회하는 API입니다.

## API 정보
- **URL**: `GET /api/posts/{postId}/comment-authors`
- **Method**: GET
- **인증**: 불필요 (공개 API)
- **설명**: 해당 게시글에 댓글을 단 모든 사용자들의 정보를 조회합니다.

## Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| postId | Long | ✅ | 게시글 ID |

## Response

### 성공 응답 (200 OK)
```json
{
  "message": "댓글 작성자 조회 완료",
  "authors": [
    {
      "id": 1,
      "username": "user123",
      "nickname": "사용자1",
      "firstCommentAt": "2025-01-20T10:30:00"
    },
    {
      "id": 2,
      "username": "user456",
      "nickname": "사용자2",
      "firstCommentAt": "2025-01-20T11:15:00"
    },
    {
      "id": 3,
      "username": "user789",
      "nickname": "사용자3",
      "firstCommentAt": "2025-01-20T14:20:00"
    }
  ],
  "success": true
}
```

### 응답 필드 설명
| 필드 | 타입 | 설명 |
|------|------|------|
| message | String | 응답 메시지 |
| authors | Array | 댓글 작성자 목록 |
| success | Boolean | 성공 여부 |

### CommentAuthorInfo 필드
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 사용자 ID |
| username | String | 사용자명 (로그인 ID) |
| nickname | String | 닉네임 |
| firstCommentAt | String | 해당 게시글에 첫 댓글을 단 시간 (ISO 8601 형식) |

### 에러 응답 (400 Bad Request)
```json
{
  "message": "댓글 작성자 조회 실패: 게시글을 찾을 수 없습니다.",
  "authors": null,
  "success": false
}
```

## 사용 예시

### cURL
```bash
curl -X GET "http://localhost:8080/api/posts/123/comment-authors" \
  -H "Content-Type: application/json"
```

### JavaScript
```javascript
fetch('/api/posts/123/comment-authors', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => {
  console.log('댓글 작성자들:', data.authors);
  data.authors.forEach(author => {
    console.log(`${author.nickname} (${author.username}) - 첫 댓글: ${author.firstCommentAt}`);
  });
})
.catch(error => console.error('Error:', error));
```

### Flutter
```dart
Future<List<CommentAuthorInfo>> getCommentAuthors(int postId) async {
  final response = await http.get(
    Uri.parse('http://localhost:8080/api/posts/$postId/comment-authors'),
    headers: {'Content-Type': 'application/json'},
  );

  if (response.statusCode == 200) {
    final data = jsonDecode(response.body);
    if (data['success'] == true) {
      return (data['authors'] as List)
          .map((author) => CommentAuthorInfo.fromJson(author))
          .toList();
    }
  }
  
  throw Exception('댓글 작성자 조회 실패');
}

class CommentAuthorInfo {
  final int id;
  final String username;
  final String nickname;
  final DateTime firstCommentAt;

  CommentAuthorInfo({
    required this.id,
    required this.username,
    required this.nickname,
    required this.firstCommentAt,
  });

  factory CommentAuthorInfo.fromJson(Map<String, dynamic> json) {
    return CommentAuthorInfo(
      id: json['id'],
      username: json['username'],
      nickname: json['nickname'],
      firstCommentAt: DateTime.parse(json['firstCommentAt']),
    );
  }
}
```

## 특징

### 1. 중복 제거
- 같은 사용자가 여러 번 댓글을 달아도 한 번만 표시됩니다.
- `DISTINCT` 쿼리를 사용하여 중복을 제거합니다.

### 2. 정렬 순서
- `firstCommentAt` 기준으로 오름차순 정렬됩니다.
- 가장 먼저 댓글을 단 사용자가 첫 번째로 표시됩니다.

### 3. 성능 최적화
- 단일 쿼리로 사용자 정보와 첫 댓글 시간을 함께 조회합니다.
- `GROUP BY`와 `MIN()` 함수를 사용하여 효율적으로 처리합니다.

## 데이터베이스 쿼리
```sql
SELECT c.user.id, c.user.username, c.user.nickname, MIN(c.createdAt) as firstCommentAt 
FROM Comment c 
WHERE c.post.id = :postId 
GROUP BY c.user.id, c.user.username, c.user.nickname 
ORDER BY firstCommentAt ASC
```

## 사용 사례

### 1. 댓글 알림 대상 확인
- 게시글에 새 댓글이 달렸을 때 알림을 보낼 대상들을 확인
- 기존 댓글 작성자들에게 알림 전송

### 2. 댓글 참여도 분석
- 게시글에 참여한 사용자 수 확인
- 가장 활발한 댓글 작성자 파악

### 3. 사용자 활동 추적
- 특정 게시글에 언제 처음 댓글을 달았는지 확인
- 사용자의 댓글 참여 패턴 분석

## 에러 처리

### 1. 게시글 없음
- 존재하지 않는 게시글 ID로 요청 시 400 에러
- 메시지: "게시글을 찾을 수 없습니다."

### 2. 댓글 없음
- 댓글이 없는 게시글의 경우 빈 배열 반환
- 에러가 아닌 정상 응답으로 처리

## 보안 고려사항

### 1. 공개 API
- 인증이 필요하지 않은 공개 API입니다.
- 모든 사용자가 접근 가능합니다.

### 2. 개인정보 노출
- 사용자 ID, 사용자명, 닉네임이 노출됩니다.
- 민감한 개인정보는 포함되지 않습니다.

## 성능 고려사항

### 1. 인덱스
- `post_id` 컬럼에 인덱스가 필요합니다.
- `user_id`, `created_at` 컬럼에도 복합 인덱스 권장

### 2. 캐싱
- 자주 조회되는 게시글의 경우 캐싱 고려
- 댓글 작성자 목록은 자주 변경되지 않음

### 3. 페이징
- 현재는 페이징이 지원되지 않습니다.
- 댓글 작성자가 많은 경우 페이징 추가 고려 