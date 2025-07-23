# 게시글 좋아요 알림 API 명세서

## 📋 **API 개요**

게시글 좋아요 버튼 클릭 시 게시글 작성자에게 실시간 알림을 발송하는 API입니다.

## 🔗 **API 엔드포인트**

### 게시글 좋아요 토글
```
POST /api/posts/{postId}/toggle-like
```

## 📝 **요청 정보**

### Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| postId | Long | ✅ | 좋아요할 게시글 ID |

### Headers
| 헤더 | 값 | 필수 | 설명 |
|------|-----|------|------|
| Authorization | Bearer {JWT_TOKEN} | ✅ | JWT 인증 토큰 |
| Content-Type | application/json | ✅ | JSON 형식 |

### Request Body
```json
// 요청 본문 없음 (Path Parameter만 사용)
```

## 📤 **응답 정보**

### 성공 응답 (200 OK)
```json
{
  "message": "게시글에 좋아요를 눌렀습니다.",
  "likeCount": 15,
  "isLiked": true,
  "isReported": false,
  "isBookmarked": false
}
```

### 좋아요 취소 응답 (200 OK)
```json
{
  "message": "게시글 좋아요가 취소되었습니다.",
  "likeCount": 14,
  "isLiked": false,
  "isReported": false,
  "isBookmarked": false
}
```

### 응답 필드 설명
| 필드 | 타입 | 설명 |
|------|------|------|
| message | String | 작업 결과 메시지 |
| likeCount | Integer | 현재 좋아요 개수 |
| isLiked | Boolean | 현재 사용자의 좋아요 상태 |
| isReported | Boolean | 현재 사용자의 신고 상태 |
| isBookmarked | Boolean | 현재 사용자의 북마크 상태 |

### 에러 응답 (400 Bad Request)
```json
{
  "message": "게시글 좋아요 토글 실패: 게시글을 찾을 수 없습니다.",
  "likeCount": 0,
  "isLiked": false,
  "isReported": false,
  "isBookmarked": false
}
```

## 🔔 **알림 기능**

### 1. 알림 발송 조건
- ✅ **좋아요 추가 시**: 게시글 작성자에게 알림 발송
- ❌ **좋아요 취소 시**: 알림 발송 안함
- ❌ **자신의 게시글**: 자신에게 좋아요를 눌러도 알림 발송 안함

### 2. 알림 메시지
```
"김철수님이 회원님의 게시글에 좋아요를 눌렀습니다."
```

### 3. 알림 설정 확인
- 사용자의 좋아요 알림 설정이 **활성화**되어 있어야 함
- 게시글이 **뮤트**되어 있으면 알림 발송 안함

### 4. 실시간 전송
- **WebSocket**을 통해 실시간으로 알림 전송
- 구독 채널: `/user/queue/notifications`

## 🧪 **사용 예시**

### cURL 예시
```bash
# 게시글 좋아요
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"

# 응답 예시
{
  "message": "게시글에 좋아요를 눌렀습니다.",
  "likeCount": 15,
  "isLiked": true,
  "isReported": false,
  "isBookmarked": false
}
```

### JavaScript 예시
```javascript
// 게시글 좋아요 토글
async function togglePostLike(postId) {
  try {
    const response = await fetch(`/api/posts/${postId}/toggle-like`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${getJwtToken()}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (response.ok) {
      console.log('좋아요 토글 성공:', result.message);
      console.log('현재 좋아요 개수:', result.likeCount);
      console.log('좋아요 상태:', result.isLiked);
    } else {
      console.error('좋아요 토글 실패:', result.message);
    }
  } catch (error) {
    console.error('API 호출 오류:', error);
  }
}

// 사용 예시
togglePostLike(1);
```

### Flutter 예시
```dart
// 게시글 좋아요 토글
Future<void> togglePostLike(int postId) async {
  try {
    final response = await http.post(
      Uri.parse('http://localhost:8080/api/posts/$postId/toggle-like'),
      headers: {
        'Authorization': 'Bearer $jwtToken',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      final result = jsonDecode(response.body);
      print('좋아요 토글 성공: ${result['message']}');
      print('현재 좋아요 개수: ${result['likeCount']}');
      print('좋아요 상태: ${result['isLiked']}');
    } else {
      final error = jsonDecode(response.body);
      print('좋아요 토글 실패: ${error['message']}');
    }
  } catch (e) {
    print('API 호출 오류: $e');
  }
}

// 사용 예시
await togglePostLike(1);
```

## 🔄 **WebSocket 알림 수신**

### 1. WebSocket 연결
```javascript
// WebSocket 연결 및 알림 구독
const stompClient = new StompJs.Client({
  webSocketFactory: () => new SockJS('http://localhost:8080/ws')
});

stompClient.connect(
  { 'Authorization': `Bearer ${jwtToken}` },
  function(frame) {
    console.log('WebSocket 연결됨');
    
    // 개인 알림 구독
    stompClient.subscribe('/user/queue/notifications', function(message) {
      const notification = JSON.parse(message.body);
      console.log('새 알림 수신:', notification);
      
      // 알림 표시
      showNotification(notification.content);
    });
  }
);
```

### 2. 알림 메시지 형식
```json
{
  "type": "notification",
  "notificationId": 123,
  "senderId": 7,
  "senderNickname": "김철수",
  "notificationType": "like",
  "title": "좋아요",
  "content": "김철수님이 회원님의 게시글에 좋아요를 눌렀습니다.",
  "targetType": "post",
  "targetId": 1,
  "read": false,
  "createdAt": "2025-07-21T10:30:00",
  "unreadCount": 5
}
```

## ⚠️ **주의사항**

### 1. 인증 필수
- JWT 토큰이 유효해야 함
- 토큰이 만료되면 401 Unauthorized 응답

### 2. 권한 확인
- 게시글이 존재해야 함
- 삭제된 게시글에는 좋아요 불가

### 3. 중복 처리
- 이미 좋아요를 눌렀으면 취소됨
- 토글 방식으로 동작

### 4. 알림 설정
- 사용자의 좋아요 알림 설정이 켜져 있어야 함
- 게시글 뮤트 시 알림 발송 안함

## 🔧 **관련 API**

### 1. 알림 설정 조회/수정
```
GET /api/notifications/settings
PUT /api/notifications/settings
```

### 2. 내가 좋아요한 게시글 목록
```
GET /api/posts/user/me/likes
```

### 3. 게시글 좋아요 사용자 목록 (관리자)
```
GET /api/posts/{postId}/likes
```

## 📊 **성능 고려사항**

### 1. 데이터베이스
- 좋아요 개수는 실시간 계산
- HOT 게시글 점수 자동 업데이트

### 2. WebSocket
- 실시간 알림 전송
- 연결이 끊어지면 알림 누락 가능

### 3. 캐싱
- 좋아요 개수 캐싱 고려
- Redis 등 사용 권장

## ✅ **테스트 시나리오**

### 1. 기본 좋아요 테스트
```bash
# 1. 사용자 A로 게시글 작성
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer TOKEN_A" \
  -d '{"title":"테스트 게시글","content":"테스트 내용"}'

# 2. 사용자 B로 좋아요 클릭
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer TOKEN_B"

# 3. 사용자 A가 WebSocket으로 알림 수신 확인
```

### 2. 좋아요 취소 테스트
```bash
# 사용자 B로 좋아요 취소
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer TOKEN_B"
```

### 3. 자신의 게시글 좋아요 테스트
```bash
# 사용자 A가 자신의 게시글에 좋아요 (알림 발송 안함)
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer TOKEN_A"
```

---

**🎉 게시글 좋아요 알림 API가 완전히 구현되어 있습니다!**

실시간 WebSocket 알림과 함께 게시글 작성자에게 좋아요 알림이 자동으로 발송됩니다. 