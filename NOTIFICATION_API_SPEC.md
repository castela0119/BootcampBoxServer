# 📱 알림 API 명세서

## 🔗 API 엔드포인트

### 1. 알림 목록 조회
```
GET /api/notifications?page=0&size=20
```

**요청 파라미터:**
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 20)

**응답:**
```json
{
  "notifications": [
    {
      "id": 1,
      "senderId": 123,
      "senderNickname": "김철수",
      "type": "comment",
      "title": "새 댓글",
      "content": "김철수님이 회원님의 게시글에 댓글을 남겼습니다.",
      "targetType": "post",
      "targetId": 456,
      "read": false,
      "createdAt": "2024-12-19T10:30:00Z"
    }
  ],
  "currentPage": 0,
  "totalPages": 5,
  "totalElements": 100,
  "hasNext": true,
  "hasPrevious": false
}
```

### 2. 읽지 않은 알림 개수 조회
```
GET /api/notifications/count
```

**응답:**
```json
{
  "unreadCount": 15
}
```

### 3. 특정 알림 읽음 처리
```
PUT /api/notifications/{id}
```

**응답:**
```json
{
  "message": "알림을 읽음 처리했습니다.",
  "success": true
}
```

### 4. 모든 알림 읽음 처리
```
PUT /api/notifications/read-all
```

**응답:**
```json
{
  "message": "모든 알림을 읽음 처리했습니다.",
  "success": true
}
```

### 5. 알림 삭제
```
DELETE /api/notifications/{id}
```

**응답:**
```json
{
  "message": "알림을 삭제했습니다.",
  "success": true
}
```

### 6. 테스트 알림 발송
```
POST /api/notifications/test
```

**응답:**
```json
{
  "message": "테스트 알림을 발송했습니다.",
  "success": true
}
```

## 📋 알림 타입

### 1. 댓글 알림 (`comment`)
- **발생 조건**: 내 게시글에 댓글이 달렸을 때
- **제목**: "새 댓글"
- **내용**: "{작성자}님이 회원님의 게시글에 댓글을 남겼습니다."
- **대상**: 게시글 상세 페이지

### 2. 좋아요 알림 (`like`)
- **발생 조건**: 내 게시글/댓글에 좋아요가 눌렸을 때
- **제목**: "좋아요"
- **내용**: "{작성자}님이 회원님의 {게시글/댓글}에 좋아요를 눌렀습니다."
- **대상**: 해당 게시글/댓글

### 3. 시스템 알림 (`system`)
- **발생 조건**: 관리자가 공지사항을 올렸을 때
- **제목**: "공지사항" 또는 "시스템 업데이트"
- **내용**: 시스템 메시지
- **대상**: 앱 내 공지사항 페이지

## 🎨 클라이언트 구현 가이드

### Flutter/Dart 예시

#### 1. 알림 모델
```dart
class Notification {
  final int id;
  final int? senderId;
  final String? senderNickname;
  final String type;
  final String title;
  final String content;
  final String? targetType;
  final int? targetId;
  final bool read;
  final DateTime createdAt;
  
  Notification({
    required this.id,
    this.senderId,
    this.senderNickname,
    required this.type,
    required this.title,
    required this.content,
    this.targetType,
    this.targetId,
    required this.read,
    required this.createdAt,
  });
  
  factory Notification.fromJson(Map<String, dynamic> json) {
    return Notification(
      id: json['id'],
      senderId: json['senderId'],
      senderNickname: json['senderNickname'],
      type: json['type'],
      title: json['title'],
      content: json['content'],
      targetType: json['targetType'],
      targetId: json['targetId'],
      read: json['read'],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }
  
  // 알림 타입별 아이콘
  IconData get icon {
    switch (type) {
      case 'comment': return Icons.comment;
      case 'like': return Icons.favorite;
      case 'system': return Icons.notifications;
      default: return Icons.notifications;
    }
  }
  
  // 알림 타입별 색상
  Color get color {
    switch (type) {
      case 'comment': return Color(0xFF6C733D);
      case 'like': return Color(0xFFF27649);
      case 'system': return Color(0xFF8A8575);
      default: return Color(0xFF6C733D);
    }
  }
}
```

#### 2. 알림 서비스
```dart
class NotificationService {
  final String baseUrl = 'http://localhost:8080/api';
  final String? token;
  
  NotificationService(this.token);
  
  // 알림 목록 조회
  Future<Map<String, dynamic>> getNotifications({int page = 0, int size = 20}) async {
    final response = await http.get(
      Uri.parse('$baseUrl/notifications?page=$page&size=$size'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
    
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('알림 목록 조회 실패');
    }
  }
  
  // 읽지 않은 알림 개수 조회
  Future<int> getUnreadCount() async {
    final response = await http.get(
      Uri.parse('$baseUrl/notifications/count'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
    
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['unreadCount'];
    } else {
      throw Exception('읽지 않은 알림 개수 조회 실패');
    }
  }
  
  // 알림 읽음 처리
  Future<void> markAsRead(int notificationId) async {
    final response = await http.put(
      Uri.parse('$baseUrl/notifications/$notificationId'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
    
    if (response.statusCode != 200) {
      throw Exception('알림 읽음 처리 실패');
    }
  }
  
  // 모든 알림 읽음 처리
  Future<void> markAllAsRead() async {
    final response = await http.put(
      Uri.parse('$baseUrl/notifications/read-all'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
    
    if (response.statusCode != 200) {
      throw Exception('모든 알림 읽음 처리 실패');
    }
  }
  
  // 알림 삭제
  Future<void> deleteNotification(int notificationId) async {
    final response = await http.delete(
      Uri.parse('$baseUrl/notifications/$notificationId'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
    
    if (response.statusCode != 200) {
      throw Exception('알림 삭제 실패');
    }
  }
}
```

#### 3. 알림 Provider
```dart
class NotificationProvider extends ChangeNotifier {
  List<Notification> _notifications = [];
  int _unreadCount = 0;
  bool _isLoading = false;
  String? _error;
  bool _hasMore = true;
  int _currentPage = 0;
  
  List<Notification> get notifications => _notifications;
  int get unreadCount => _unreadCount;
  bool get isLoading => _isLoading;
  String? get error => _error;
  bool get hasMore => _hasMore;
  
  final NotificationService _notificationService;
  
  NotificationProvider(this._notificationService);
  
  // 알림 목록 로드
  Future<void> loadNotifications({bool refresh = false}) async {
    if (_isLoading) return;
    
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      if (refresh) {
        _currentPage = 0;
        _notifications.clear();
        _hasMore = true;
      }
      
      if (!_hasMore) return;
      
      final data = await _notificationService.getNotifications(
        page: _currentPage,
        size: 20,
      );
      
      final List<Notification> newNotifications = (data['notifications'] as List)
          .map((json) => Notification.fromJson(json))
          .toList();
      
      if (refresh) {
        _notifications = newNotifications;
      } else {
        _notifications.addAll(newNotifications);
      }
      
      _currentPage = data['currentPage'] + 1;
      _hasMore = data['hasNext'];
      
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
  
  // 읽지 않은 알림 개수 로드
  Future<void> loadUnreadCount() async {
    try {
      _unreadCount = await _notificationService.getUnreadCount();
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }
  
  // 알림 읽음 처리
  Future<void> markAsRead(int notificationId) async {
    try {
      await _notificationService.markAsRead(notificationId);
      
      // 로컬 상태 업데이트
      final index = _notifications.indexWhere((n) => n.id == notificationId);
      if (index != -1) {
        _notifications[index] = Notification(
          id: _notifications[index].id,
          senderId: _notifications[index].senderId,
          senderNickname: _notifications[index].senderNickname,
          type: _notifications[index].type,
          title: _notifications[index].title,
          content: _notifications[index].content,
          targetType: _notifications[index].targetType,
          targetId: _notifications[index].targetId,
          read: true,
          createdAt: _notifications[index].createdAt,
        );
      }
      
      // 읽지 않은 개수 업데이트
      await loadUnreadCount();
      
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }
  
  // 모든 알림 읽음 처리
  Future<void> markAllAsRead() async {
    try {
      await _notificationService.markAllAsRead();
      
      // 로컬 상태 업데이트
      _notifications = _notifications.map((notification) => Notification(
        id: notification.id,
        senderId: notification.senderId,
        senderNickname: notification.senderNickname,
        type: notification.type,
        title: notification.title,
        content: notification.content,
        targetType: notification.targetType,
        targetId: notification.targetId,
        read: true,
        createdAt: notification.createdAt,
      )).toList();
      
      _unreadCount = 0;
      notifyListeners();
      
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }
  
  // 알림 삭제
  Future<void> deleteNotification(int notificationId) async {
    try {
      await _notificationService.deleteNotification(notificationId);
      
      // 로컬 상태에서 제거
      _notifications.removeWhere((n) => n.id == notificationId);
      
      // 읽지 않은 개수 업데이트
      await loadUnreadCount();
      
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }
}
```

## 🔄 실시간 알림 구현 (선택사항)

### WebSocket 연결
```dart
class NotificationWebSocket {
  WebSocketChannel? _channel;
  final Function(Notification) onNotificationReceived;
  final Function() onConnectionError;
  final Function() onConnectionClosed;
  
  NotificationWebSocket({
    required this.onNotificationReceived,
    required this.onConnectionError,
    required this.onConnectionClosed,
  });
  
  void connect(String token) {
    final uri = Uri.parse('ws://localhost:8080/ws/notifications?token=$token');
    _channel = WebSocketChannel.connect(uri);
    
    _channel!.stream.listen(
      (data) {
        final message = jsonDecode(data);
        if (message['type'] == 'NEW_NOTIFICATION') {
          final notification = Notification.fromJson(message['data']);
          onNotificationReceived(notification);
        }
      },
      onError: (error) {
        print('WebSocket 오류: $error');
        onConnectionError();
        // 5초 후 재연결 시도
        Future.delayed(Duration(seconds: 5), () => connect(token));
      },
      onDone: () {
        print('WebSocket 연결 종료');
        onConnectionClosed();
        // 5초 후 재연결 시도
        Future.delayed(Duration(seconds: 5), () => connect(token));
      },
    );
  }
  
  void disconnect() {
    _channel?.sink.close();
  }
}
```

## 📱 UI 구현 예시

### 알림 목록 화면
```dart
class NotificationsScreen extends StatefulWidget {
  @override
  _NotificationsScreenState createState() => _NotificationsScreenState();
}

class _NotificationsScreenState extends State<NotificationsScreen> {
  final ScrollController _scrollController = ScrollController();
  
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<NotificationProvider>().loadNotifications(refresh: true);
      context.read<NotificationProvider>().loadUnreadCount();
    });
    
    _scrollController.addListener(_onScroll);
  }
  
  void _onScroll() {
    if (_scrollController.position.pixels >= 
        _scrollController.position.maxScrollExtent - 200) {
      context.read<NotificationProvider>().loadNotifications();
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('알림'),
        actions: [
          Consumer<NotificationProvider>(
            builder: (context, provider, child) {
              if (provider.unreadCount > 0) {
                return TextButton(
                  onPressed: () => provider.markAllAsRead(),
                  child: Text('모두 읽음'),
                );
              }
              return SizedBox.shrink();
            },
          ),
        ],
      ),
      body: Consumer<NotificationProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.notifications.isEmpty) {
            return Center(child: CircularProgressIndicator());
          }
          
          if (provider.error != null && provider.notifications.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text('오류가 발생했습니다'),
                  Text(provider.error!),
                  ElevatedButton(
                    onPressed: () => provider.loadNotifications(refresh: true),
                    child: Text('다시 시도'),
                  ),
                ],
              ),
            );
          }
          
          if (provider.notifications.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.notifications_none, size: 64, color: Colors.grey),
                  SizedBox(height: 16),
                  Text('알림이 없습니다', style: TextStyle(fontSize: 18)),
                ],
              ),
            );
          }
          
          return RefreshIndicator(
            onRefresh: () => provider.loadNotifications(refresh: true),
            child: ListView.builder(
              controller: _scrollController,
              itemCount: provider.notifications.length + (provider.hasMore ? 1 : 0),
              itemBuilder: (context, index) {
                if (index == provider.notifications.length) {
                  return Center(
                    child: Padding(
                      padding: EdgeInsets.all(16),
                      child: CircularProgressIndicator(),
                    ),
                  );
                }
                
                final notification = provider.notifications[index];
                return NotificationCard(
                  notification: notification,
                  onTap: () => provider.markAsRead(notification.id),
                  onDelete: () => provider.deleteNotification(notification.id),
                );
              },
            ),
          );
        },
      ),
    );
  }
}
```

### 알림 카드 위젯
```dart
class NotificationCard extends StatelessWidget {
  final Notification notification;
  final VoidCallback onTap;
  final VoidCallback onDelete;
  
  const NotificationCard({
    Key? key,
    required this.notification,
    required this.onTap,
    required this.onDelete,
  }) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      color: notification.read ? Colors.white : Colors.blue.shade50,
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: notification.color,
          child: Icon(notification.icon, color: Colors.white),
        ),
        title: Text(
          notification.title,
          style: TextStyle(
            fontWeight: notification.read ? FontWeight.normal : FontWeight.bold,
          ),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(notification.content),
            SizedBox(height: 4),
            Text(
              _formatTime(notification.createdAt),
              style: TextStyle(fontSize: 12, color: Colors.grey),
            ),
          ],
        ),
        trailing: PopupMenuButton(
          itemBuilder: (context) => [
            PopupMenuItem(
              value: 'delete',
              child: Text('삭제'),
            ),
          ],
          onSelected: (value) {
            if (value == 'delete') {
              onDelete();
            }
          },
        ),
        onTap: onTap,
      ),
    );
  }
  
  String _formatTime(DateTime time) {
    final now = DateTime.now();
    final difference = now.difference(time);
    
    if (difference.inDays > 0) {
      return '${difference.inDays}일 전';
    } else if (difference.inHours > 0) {
      return '${difference.inHours}시간 전';
    } else if (difference.inMinutes > 0) {
      return '${difference.inMinutes}분 전';
    } else {
      return '방금 전';
    }
  }
}
```

## 🚀 성능 최적화

### 1. 페이징
- 한 번에 20개씩 로드
- 스크롤 시 추가 로드

### 2. 캐싱
- 로컬에 알림 데이터 캐시
- 오프라인에서도 확인 가능

### 3. 실시간 업데이트
- WebSocket으로 실시간 통신
- 불필요한 API 호출 최소화

### 4. 메모리 관리
- 오래된 알림 자동 삭제 (30일)
- 이미지 lazy loading

---

이제 완전한 알림 시스템이 구현되었습니다! 🎉 