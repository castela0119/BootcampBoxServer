# WebSocket API 명세서

## 개요
실시간 알림을 위한 WebSocket API입니다. 클라이언트는 WebSocket 연결을 통해 실시간으로 알림을 받을 수 있습니다.

## 연결 정보
- **WebSocket URL**: `ws://localhost:8080/ws`
- **SockJS URL**: `http://localhost:8080/ws`
- **프로토콜**: STOMP over WebSocket

## 인증
WebSocket 연결 시 JWT 토큰을 헤더에 포함해야 합니다:
```
Authorization: Bearer <JWT_TOKEN>
```

## 메시지 형식

### 1. 알림 메시지
```json
{
  "type": "notification",
  "notificationId": 123,
  "senderId": 456,
  "senderNickname": "사용자닉네임",
  "notificationType": "comment",
  "title": "새 댓글",
  "content": "게시글에 댓글이 달렸습니다.",
  "targetType": "post",
  "targetId": 789,
  "read": false,
  "createdAt": "2025-01-20T10:30:00",
  "unreadCount": 5
}
```

### 2. 읽지 않은 알림 개수 메시지
```json
{
  "type": "unread_count",
  "unreadCount": 3
}
```

### 3. 연결 상태 메시지
```json
{
  "type": "connection",
  "status": "connected",
  "username": "user123"
}
```

## 구독 채널

### 1. 개인 알림 구독
```
/user/queue/notifications
```
- 특정 사용자에게 전송되는 알림을 받습니다.
- 댓글, 좋아요 등의 알림이 이 채널로 전송됩니다.

### 2. 읽지 않은 알림 개수 구독
```
/user/queue/unread-count
```
- 읽지 않은 알림 개수 업데이트를 받습니다.
- 알림을 읽음 처리할 때마다 업데이트됩니다.

### 3. 연결 상태 구독
```
/user/queue/connection
```
- WebSocket 연결 상태를 받습니다.

### 4. 공개 채널 (테스트용)
```
/topic/echo
```
- 모든 연결된 클라이언트에게 브로드캐스트됩니다.

## 메시지 전송

### 1. 연결 확인
```
/app/connect
```
- 연결 확인 메시지를 서버로 전송합니다.
- 응답: `/user/queue/connection`

### 2. 에코 테스트
```
/app/echo
```
- 테스트 메시지를 서버로 전송합니다.
- 응답: `/topic/echo`

## 알림 수신 대상 (업데이트됨)

### 댓글 알림
**이전**: 게시글 작성자에게만 알림
**현재**: 다음 사용자들에게 알림
1. **게시글 작성자** ✅
2. **해당 게시글에 댓글을 단 모든 사용자** ✅
3. **댓글 작성자 본인** ❌ (자신에게는 알림 안 보냄)

### 예시 시나리오
게시글 ID: 123
- 게시글 작성자: 사용자A
- 기존 댓글 작성자들: 사용자B, 사용자C, 사용자D

**사용자E가 새 댓글 작성 시:**
- ✅ 사용자A (게시글 작성자) → 알림 수신
- ✅ 사용자B (기존 댓글 작성자) → 알림 수신  
- ✅ 사용자C (기존 댓글 작성자) → 알림 수신
- ✅ 사용자D (기존 댓글 작성자) → 알림 수신
- ❌ 사용자E (댓글 작성자 본인) → 알림 수신 안함

### 좋아요 알림
- **게시글 좋아요**: 게시글 작성자에게만 알림
- **댓글 좋아요**: 댓글 작성자에게만 알림

## 클라이언트 연결 예시 (JavaScript)

```javascript
// SockJS와 STOMP 라이브러리 필요
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// JWT 토큰
const token = 'your-jwt-token';

// 연결
stompClient.connect(
  { 'Authorization': 'Bearer ' + token },
  function (frame) {
    console.log('Connected: ' + frame);
    
    // 개인 알림 구독
    stompClient.subscribe('/user/queue/notifications', function (notification) {
      const data = JSON.parse(notification.body);
      console.log('새 알림:', data);
      // 알림 처리 로직
    });
    
    // 읽지 않은 알림 개수 구독
    stompClient.subscribe('/user/queue/unread-count', function (count) {
      const data = JSON.parse(count.body);
      console.log('읽지 않은 알림 개수:', data.unreadCount);
      // UI 업데이트
    });
    
    // 연결 확인
    stompClient.send("/app/connect", {}, "user123");
  },
  function (error) {
    console.log('STOMP error: ' + error);
  }
);
```

## Flutter 클라이언트 예시

```dart
import 'package:web_socket_channel/web_socket_channel.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';

class WebSocketService {
  StompClient? stompClient;
  String? jwtToken;
  
  void connect(String token) {
    jwtToken = token;
    
    stompClient = StompClient(
      config: StompConfig(
        url: 'ws://localhost:8080/ws',
        onConnect: onConnect,
        onDisconnect: onDisconnect,
        onWebSocketError: (dynamic error) => print(error.toString()),
        stompConnectHeaders: {'Authorization': 'Bearer $token'},
        webSocketConnectHeaders: {'Authorization': 'Bearer $token'},
      ),
    );
    
    stompClient!.activate();
  }
  
  void onConnect(StompFrame frame) {
    print('WebSocket 연결됨');
    
    // 개인 알림 구독
    stompClient!.subscribe(
      destination: '/user/queue/notifications',
      callback: (frame) {
        final notification = jsonDecode(frame.body!);
        print('새 알림: $notification');
        // 알림 처리
      },
    );
    
    // 읽지 않은 알림 개수 구독
    stompClient!.subscribe(
      destination: '/user/queue/unread-count',
      callback: (frame) {
        final count = jsonDecode(frame.body!);
        print('읽지 않은 알림 개수: ${count['unreadCount']}');
        // UI 업데이트
      },
    );
  }
  
  void onDisconnect(StompFrame frame) {
    print('WebSocket 연결 해제됨');
  }
  
  void disconnect() {
    stompClient?.deactivate();
  }
}
```

## 클라이언트 최적화 권장사항

### 1. 알림 중복 방지
```dart
// 같은 게시글의 연속된 댓글 알림 중복 방지
Set<String> _processedNotifications = {};

void handleNotification(Notification notification) {
  String key = "${notification.type}_${notification.targetId}_${notification.senderId}";
  if (_processedNotifications.contains(key)) return;
  
  _processedNotifications.add(key);
  // 알림 처리
}
```

### 2. 알림 그룹핑
```dart
// 같은 게시글의 여러 댓글을 그룹으로 표시
class GroupedNotification {
  String postTitle;
  List<Notification> notifications;
  int totalCount;
}
```

### 3. 알림 설정
```dart
// 사용자가 알림 유형별로 설정 가능
class NotificationSettings {
  bool commentFromAuthor = true;      // 게시글 작성자 댓글
  bool commentFromOthers = true;      // 다른 사용자 댓글
  bool likeNotifications = true;      // 좋아요 알림
}
```

## 서버 로그 예시

```
2025-01-20 10:30:00.123 INFO  - WebSocket 연결 인증 성공 - 사용자: user123
2025-01-20 10:30:15.456 INFO  - 댓글 알림 생성 - 수신자: user456, 발신자: user123, 게시글: 789
2025-01-20 10:30:15.457 INFO  - 댓글 알림 생성 - 수신자: user789, 발신자: user123, 게시글: 789
2025-01-20 10:30:15.458 INFO  - 댓글 알림 전송 완료 - 발신자: user123, 게시글: 789, 수신자 수: 3
2025-01-20 10:30:15.459 INFO  - WebSocket 알림 전송 완료 - 사용자: user456, 알림 ID: 123
2025-01-20 10:30:15.460 INFO  - WebSocket 알림 전송 완료 - 사용자: user789, 알림 ID: 124
2025-01-20 10:30:20.789 INFO  - WebSocket 읽지 않은 알림 개수 전송 완료 - 사용자: user456, 개수: 5
```

## 에러 처리

### 1. 인증 실패
- JWT 토큰이 없거나 유효하지 않은 경우 연결이 거부됩니다.
- 로그: "WebSocket 연결 인증 실패"

### 2. 연결 오류
- 네트워크 오류나 서버 오류 시 연결이 끊어집니다.
- 클라이언트는 재연결 로직을 구현해야 합니다.

### 3. 메시지 전송 실패
- 수신자가 오프라인이거나 연결이 끊어진 경우 메시지 전송이 실패할 수 있습니다.
- 서버는 데이터베이스에 알림을 저장하므로 나중에 조회할 수 있습니다.

## 보안 고려사항

1. **JWT 토큰 검증**: 모든 WebSocket 연결은 JWT 토큰으로 인증됩니다.
2. **사용자별 메시지**: 개인 알림은 해당 사용자에게만 전송됩니다.
3. **CORS 설정**: 개발환경에서만 모든 origin을 허용합니다.
4. **토큰 만료**: JWT 토큰이 만료되면 연결이 끊어집니다.

## 성능 고려사항

1. **연결 풀 관리**: 많은 동시 연결을 처리할 수 있도록 설정을 조정합니다.
2. **메시지 큐**: 대량의 메시지 전송 시 큐 시스템을 고려합니다.
3. **하트비트**: 연결 상태를 주기적으로 확인합니다.
4. **재연결**: 클라이언트는 연결이 끊어졌을 때 자동으로 재연결해야 합니다.
5. **알림 빈도**: 댓글이 많은 게시글에서는 알림 빈도가 높아질 수 있으므로 클라이언트에서 적절한 처리 필요 