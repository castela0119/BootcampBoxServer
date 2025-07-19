# 대댓글 페이지네이션 API 명세서 (선택적 구현)

## 📋 개요

대댓글이 매우 많아져서 페이지네이션이 필요한 경우를 위한 API 명세서입니다. 현재는 대댓글을 모두 한 번에 로딩하지만, 필요시 이 API를 추가로 구현할 수 있습니다.

## 🔗 추가 API 엔드포인트

### 대댓글 페이지네이션
- **GET** `/api/posts/comments/{commentId}/replies` - 대댓글 목록 조회 (페이징)

---

## 📝 대댓글 페이지네이션 API 상세

### 대댓글 목록 조회

#### 엔드포인트
```
GET /api/posts/comments/{commentId}/replies
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| `commentId` | Long | ✅ | - | 부모 댓글 ID (Path Variable) |
| `page` | Integer | ❌ | 0 | 페이지 번호 (0부터 시작) |
| `size` | Integer | ❌ | 10 | 페이지당 대댓글 수 |

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/posts/comments/32/replies?page=0&size=5" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 응답 예시
```json
{
  "content": [
    {
      "id": 45,
      "content": "동감합니다!",
      "authorNickname": "사용자1",
      "authorUsername": "user1@example.com",
      "authorId": 8,
      "postId": 46,
      "parentId": 32,
      "createdAt": "2025-07-19T13:35:00",
      "updatedAt": null,
      "replies": [],
      "likeCount": 2,
      "liked": false,
      "author": false
    },
    {
      "id": 46,
      "content": "정말 좋은 의견이네요!",
      "authorNickname": "사용자2",
      "authorUsername": "user2@example.com",
      "authorId": 9,
      "postId": 46,
      "parentId": 32,
      "createdAt": "2025-07-19T13:40:00",
      "updatedAt": null,
      "replies": [],
      "likeCount": 1,
      "liked": true,
      "author": false
    }
  ],
  "totalElements": 8,
  "totalPages": 2,
  "size": 5,
  "number": 0,
  "first": true,
  "last": false,
  "numberOfElements": 5,
  "hasNext": true,
  "hasPrevious": false
}
```

---

## 🔧 구현 방법

### 1. Repository 메서드 추가
```java
// CommentRepository.java
@Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId ORDER BY c.createdAt ASC")
Page<Comment> findByParentIdOrderByCreatedAtAsc(@Param("parentId") Long parentId, Pageable pageable);
```

### 2. Service 메서드 추가
```java
// CommentService.java
public CommentDto.CommentListResponse getReplies(Long commentId, int page, int size, String username) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Comment> replyPage = commentRepository.findByParentIdOrderByCreatedAtAsc(commentId, pageable);
    
    final Long currentUserId = userRepository.findByUsername(username)
            .map(User::getId)
            .orElse(null);
    
    List<CommentDto.CommentResponse> replyResponses = replyPage.getContent().stream()
        .map(reply -> {
            boolean isLiked = false;
            if (currentUserId != null) {
                isLiked = reply.getLikedUsers().stream()
                        .anyMatch(user -> user.getId().equals(currentUserId));
            }
            return CommentDto.CommentResponse.from(reply, currentUserId, isLiked);
        })
        .collect(Collectors.toList());

    return new CommentDto.CommentListResponse(
        replyResponses,
        replyPage.getTotalElements(),
        replyPage.getNumber(),
        replyPage.getTotalPages(),
        replyPage.hasNext(),
        replyPage.hasPrevious()
    );
}
```

### 3. Controller 엔드포인트 추가
```java
// CommentController.java
@GetMapping("/comments/{commentId}/replies")
public ResponseEntity<CommentDto.CommentListResponse> getReplies(
        @PathVariable Long commentId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        log.info("대댓글 목록 조회 요청 - 댓글: {}, 페이지: {}, 크기: {}, 사용자: {}", commentId, page, size, username);
        CommentDto.CommentListResponse response = commentService.getReplies(commentId, page, size, username);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        log.error("대댓글 목록 조회 오류: ", e);
        return ResponseEntity.badRequest().build();
    }
}
```

---

## 🎯 클라이언트 구현 가이드

### Flutter/Dart 구현 예시
```dart
class CommentReplyService {
  final Dio dio;
  
  CommentReplyService(this.dio);
  
  Future<CommentListResponse> getReplies(int commentId, {int page = 0, int size = 10}) async {
    final response = await dio.get(
      '/api/posts/comments/$commentId/replies',
      queryParameters: {
        'page': page,
        'size': size,
      },
    );
    
    return CommentListResponse.fromJson(response.data);
  }
}

// 사용 예시
class CommentWidget extends StatefulWidget {
  final int commentId;
  
  @override
  _CommentWidgetState createState() => _CommentWidgetState();
}

class _CommentWidgetState extends State<CommentWidget> {
  List<CommentResponse> replies = [];
  int currentPage = 0;
  bool hasMore = true;
  bool isLoading = false;
  
  @override
  void initState() {
    super.initState();
    _loadReplies();
  }
  
  Future<void> _loadReplies({bool refresh = false}) async {
    if (isLoading) return;
    
    setState(() {
      isLoading = true;
    });
    
    try {
      final response = await commentReplyService.getReplies(
        widget.commentId,
        page: refresh ? 0 : currentPage,
        size: 10,
      );
      
      setState(() {
        if (refresh) {
          replies = response.content;
          currentPage = 0;
        } else {
          replies.addAll(response.content);
          currentPage++;
        }
        hasMore = response.hasNext;
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        isLoading = false;
      });
      print('대댓글 로딩 오류: $e');
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // 부모 댓글 표시
        CommentItemWidget(comment: parentComment),
        
        // 대댓글 목록
        ...replies.map((reply) => CommentItemWidget(
          comment: reply,
          isReply: true,
        )),
        
        // 더보기 버튼
        if (hasMore)
          ElevatedButton(
            onPressed: () => _loadReplies(),
            child: Text('대댓글 더보기'),
          ),
        
        // 로딩 인디케이터
        if (isLoading)
          CircularProgressIndicator(),
      ],
    );
  }
}
```

---

## ⚠️ 고려사항

### 1. **성능 영향**
- 별도 API 호출로 인한 네트워크 오버헤드
- 클라이언트에서 대댓글 상태 관리 복잡도 증가
- 부모 댓글과 대댓글 간 동기화 문제

### 2. **UX 영향**
- 대댓글 로딩 시 추가 로딩 시간
- "더보기" 버튼으로 인한 추가 클릭
- 대화 맥락 파악의 어려움

### 3. **구현 복잡도**
- 클라이언트에서 대댓글 페이징 로직 구현 필요
- 부모 댓글과 대댓글 간의 상태 동기화
- 에러 처리 및 재시도 로직

---

## 🎯 권장사항

### 현재 방식 유지 권장
- **대댓글 수가 50개 이하**: 현재 방식 유지
- **대댓글 수가 100개 이상**: 페이지네이션 API 추가 고려
- **성능 이슈 발생 시**: 페이지네이션 API 구현

### 하이브리드 방식
```java
// 대댓글 수에 따라 동적 처리
if (replyCount <= 20) {
    // 모든 대댓글을 한 번에 로딩
    return loadAllReplies(commentId);
} else {
    // 페이지네이션 API 사용
    return loadRepliesWithPaging(commentId, page, size);
}
```

---

**결론: 현재는 대댓글 페이지네이션 API가 필요하지 않으며, 대댓글 수가 매우 많아질 때만 고려하면 됩니다!** 🚀 