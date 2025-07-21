# 클라이언트 WebSocket 구현 가이드

## 🚨 **중요: 클라이언트 수정 필요**

기존 HTTP API만 사용하던 방식에서 **WebSocket 연결이 추가**되었습니다.
실시간 알림을 받으려면 클라이언트에서 WebSocket 연결을 구현해야 합니다.

## 📦 **필요한 패키지**

### Flutter
```yaml
# pubspec.yaml
dependencies:
  web_socket_channel: ^2.4.0
  stomp_dart_client: ^0.4.3
```

### JavaScript/React Native
```html
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
```

## 🔌 **WebSocket 연결 구현**

### Flutter 예시
```dart
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';
import 'dart:convert';

class WebSocketService {
  StompClient? stompClient;
  String? jwtToken;
  
  void connect(String token) {
    jwtToken = token;
    
    stompClient = StompClient(
      config: StompConfig(
        url: 'ws://localhost:8080/ws', // 서버 URL에 맞게 수정
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
        // 여기서 알림 처리 (NotificationProvider에 전달)
        _handleNewNotification(notification);
      },
    );
    
    // 읽지 않은 알림 개수 구독
    stompClient!.subscribe(
      destination: '/user/queue/unread-count',
      callback: (frame) {
        final count = jsonDecode(frame.body!);
        print('읽지 않은 알림 개수: ${count['unreadCount']}');
        // UI 업데이트
        _updateUnreadCount(count['unreadCount']);
      },
    );
  }
  
  void onDisconnect(StompFrame frame) {
    print('WebSocket 연결 해제됨');
  }
  
  void disconnect() {
    stompClient?.deactivate();
  }
  
  void _handleNewNotification(Map<String, dynamic> notification) {
    // NotificationProvider에 새 알림 전달
    // 예: Provider.of<NotificationProvider>(context, listen: false).addNotification(notification);
  }
  
  void _updateUnreadCount(int count) {
    // 읽지 않은 알림 개수 업데이트
    // 예: Provider.of<NotificationProvider>(context, listen: false).updateUnreadCount(count);
  }
}
```

### JavaScript 예시
```javascript
// SockJS와 STOMP 라이브러리 필요
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// JWT 토큰 (로그인 후 받은 토큰)
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
      handleNewNotification(data);
    });
    
    // 읽지 않은 알림 개수 구독
    stompClient.subscribe('/user/queue/unread-count', function (count) {
      const data = JSON.parse(count.body);
      console.log('읽지 않은 알림 개수:', data.unreadCount);
      // UI 업데이트
      updateUnreadCount(data.unreadCount);
    });
  },
  function (error) {
    console.log('STOMP error: ' + error);
  }
);
```

## 🔄 **기존 NotificationProvider 수정**

### 1. WebSocket 연결 통합
```dart
class NotificationProvider extends ChangeNotifier {
  final WebSocketService _webSocketService = WebSocketService();
  List<Notification> _notifications = [];
  int _unreadCount = 0;
  
  // 로그인 후 WebSocket 연결
  void connectWebSocket(String jwtToken) {
    _webSocketService.connect(jwtToken);
  }
  
  // 로그아웃 시 WebSocket 연결 해제
  void disconnectWebSocket() {
    _webSocketService.disconnect();
  }
  
  // WebSocket으로 받은 새 알림 추가
  void addNotificationFromWebSocket(Map<String, dynamic> notificationData) {
    final notification = Notification.fromJson(notificationData);
    _notifications.insert(0, notification);
    _unreadCount++;
    notifyListeners();
  }
  
  // WebSocket으로 받은 읽지 않은 개수 업데이트
  void updateUnreadCountFromWebSocket(int count) {
    _unreadCount = count;
    notifyListeners();
  }
}
```

### 2. 앱 시작 시 WebSocket 연결
```dart
// main.dart 또는 로그인 성공 후
void onLoginSuccess(String jwtToken) {
  // 기존 HTTP API 호출
  notificationProvider.loadNotifications();
  notificationProvider.loadUnreadCount();
  
  // WebSocket 연결 추가
  notificationProvider.connectWebSocket(jwtToken);
}
```

## 📱 **알림 수신 대상 변경**

### 이전 vs 현재
**이전**: 게시글 작성자만 댓글 알림 받음
**현재**: 
- ✅ 게시글 작성자
- ✅ 해당 게시글에 댓글을 단 모든 사용자
- ❌ 댓글 작성자 본인

### 예시
게시글 ID: 123
- 게시글 작성자: 사용자A
- 기존 댓글 작성자들: 사용자B, 사용자C, 사용자D

**사용자E가 새 댓글 작성 시:**
- 사용자A, B, C, D 모두 알림 수신
- 사용자E는 본인이 작성한 댓글이므로 알림 수신 안함

## ⚠️ **주의사항**

### 1. 알림 빈도 증가
- 인기 있는 게시글에서는 알림이 많이 올 수 있음
- 클라이언트에서 적절한 처리 필요

### 2. 연결 관리
- 앱 백그라운드 진입 시 연결 유지 여부 결정
- 네트워크 변경 시 재연결 로직 필요
- JWT 토큰 만료 시 재연결 필요

### 3. 성능 최적화
```dart
// 알림 중복 방지
Set<String> _processedNotifications = {};

void handleNotification(Notification notification) {
  String key = "${notification.type}_${notification.targetId}_${notification.senderId}";
  if (_processedNotifications.contains(key)) return;
  
  _processedNotifications.add(key);
  // 알림 처리
}
```

## 🧪 **테스트 방법**

1. **서버 실행**: `./run-local.sh`
2. **로그인**: JWT 토큰 획득
3. **WebSocket 연결**: 토큰으로 연결
4. **알림 테스트**: 
   - 다른 사용자로 댓글 작성
   - 같은 게시글에 여러 사용자가 댓글 작성
   - 알림 수신 확인

## 📞 **연락처**

구현 중 문제가 발생하면 서버 개발팀에 문의하세요.
- WebSocket 연결 문제
- 알림 수신 문제
- 성능 최적화 관련

---

**중요**: 이 가이드에 따라 클라이언트를 수정하지 않으면 실시간 알림을 받을 수 없습니다! 