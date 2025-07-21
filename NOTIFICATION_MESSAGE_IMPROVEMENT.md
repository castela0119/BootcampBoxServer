# 댓글 알림 메시지 개선 사항

## 🔧 **서버 측 수정 완료**

### 1. 문제 상황
- **기존**: 모든 수신자에게 "회원님의 게시글에 댓글을 남겼습니다" 표시
- **문제**: 게시글 작성자가 아닌 다른 댓글 작성자에게도 "회원님의 게시글"이라고 표시됨

### 2. 수정 내용

#### A. Notification 엔티티 수정
```java
// 기존 메서드
public static Notification createCommentNotification(User recipient, User sender, Long postId)

// 수정된 메서드
public static Notification createCommentNotification(User recipient, User sender, Long postId, Long postAuthorId, String postTitle)
```

#### B. 알림 메시지 로직 개선
```java
// 수신자가 게시글 작성자인지 확인하여 다른 메시지 표시
String notificationContent;
if (recipient.getId().equals(postAuthorId)) {
    // 수신자가 게시글 작성자인 경우
    notificationContent = sender.getNickname() + "님이 회원님의 게시글에 댓글을 남겼습니다.";
} else {
    // 수신자가 게시글 작성자가 아닌 경우 (다른 댓글 작성자)
    notificationContent = sender.getNickname() + "님이 [" + postTitle + "]에 댓글을 남겼습니다.";
}
```

#### C. NotificationService 수정
- `PostRepository` 의존성 추가
- 게시글 정보 조회 후 알림 생성 시 전달

## 📱 **클라이언트 측 영향**

### 1. 변경사항 없음
- **API 응답 구조**: 동일
- **WebSocket 메시지 구조**: 동일
- **클라이언트 코드 수정**: 불필요

### 2. 개선된 사용자 경험
- **게시글 작성자**: "김철수님이 회원님의 게시글에 댓글을 남겼습니다."
- **다른 댓글 작성자**: "김철수님이 [게시글 제목]에 댓글을 남겼습니다."

## 🧪 **테스트 방법**

### 1. 서버 실행
```bash
./run-local.sh
```

### 2. 테스트 시나리오
```bash
# 1. 사용자 A로 게시글 작성
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_A" \
  -d '{"title":"테스트 게시글","content":"테스트 내용"}'

# 2. 사용자 B로 댓글 작성 (게시글 작성자 A에게 알림)
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_B" \
  -d '{"content":"첫 번째 댓글"}'

# 3. 사용자 C로 댓글 작성 (게시글 작성자 A + 댓글 작성자 B에게 알림)
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_C" \
  -d '{"content":"두 번째 댓글"}'
```

### 3. 예상 결과
- **사용자 A (게시글 작성자)**: "김철수님이 회원님의 게시글에 댓글을 남겼습니다."
- **사용자 B (댓글 작성자)**: "김철수님이 [테스트 게시글]에 댓글을 남겼습니다."

## 📋 **알림 수신자 정리**

### 1. 댓글 알림 수신 대상
- ✅ **게시글 작성자**: "회원님의 게시글에 댓글을 남겼습니다."
- ✅ **기존 댓글 작성자들**: "[게시글 제목]에 댓글을 남겼습니다."
- ❌ **현재 댓글 작성자**: 자신에게는 알림 발송 안함

### 2. 알림 메시지 예시
```
게시글 제목: "오늘 날씨가 정말 좋네요!"

사용자 A (게시글 작성자)가 받는 알림:
- "김철수님이 회원님의 게시글에 댓글을 남겼습니다."
- "박영희님이 회원님의 게시글에 댓글을 남겼습니다."

사용자 B (댓글 작성자)가 받는 알림:
- "박영희님이 [오늘 날씨가 정말 좋네요!]에 댓글을 남겼습니다."
```

## 🔄 **WebSocket 연결 확인**

### 1. 연결 상태 확인
```javascript
// 클라이언트에서 WebSocket 연결 상태 확인
if (stompClient.connected) {
    console.log("WebSocket 연결됨");
} else {
    console.log("WebSocket 연결 안됨");
}
```

### 2. 알림 구독 확인
```javascript
// 개인 알림 구독
stompClient.subscribe('/user/queue/notifications', function(message) {
    const notification = JSON.parse(message.body);
    console.log('새 알림 수신:', notification);
    
    // 알림 메시지 표시
    showNotification(notification.content);
});
```

## ⚠️ **주의사항**

### 1. 게시글 제목 길이
- 게시글 제목이 너무 길 경우 UI에서 적절히 처리 필요
- 클라이언트에서 제목 길이 제한 또는 말줄임표 처리 권장

### 2. 특수문자 처리
- 게시글 제목에 특수문자가 포함된 경우 정상 표시 확인
- HTML 이스케이프 처리 필요 시 클라이언트에서 처리

### 3. 성능 고려사항
- 게시글 정보 조회가 추가되어 약간의 성능 영향 있음
- 캐싱 적용 시 고려 필요

## ✅ **완료 사항**

- [x] 서버 측 알림 메시지 로직 수정
- [x] 게시글 작성자와 댓글 작성자 구분
- [x] 적절한 알림 메시지 생성
- [x] WebSocket 실시간 전송 유지
- [x] 기존 API 호환성 유지

---

**🎉 댓글 알림 메시지 개선이 완료되었습니다!**

클라이언트에서는 별도의 수정 없이 개선된 알림 메시지를 받을 수 있습니다. 