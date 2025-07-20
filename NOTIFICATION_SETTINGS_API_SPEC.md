# 🔧 알림 설정 API 상세 명세

## 📋 API 개요

### 기본 정보
- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **인증**: Bearer Token (JWT)

### 공통 응답 형식
```json
{
  "success": true,
  "message": "성공 메시지",
  "data": {},
  "error": null
}
```

## 📥 GET /api/notifications/settings

### 설명
현재 로그인한 사용자의 알림 설정을 조회합니다.

### 요청 파라미터
없음

### 요청 헤더
| 헤더 | 값 | 필수 |
|------|-----|------|
| Authorization | Bearer {JWT_TOKEN} | ✅ |
| Content-Type | application/json | ✅ |

### 응답 예시
```json
{
  "success": true,
  "message": "알림 설정을 성공적으로 조회했습니다.",
  "data": {
    "settings": {
      "comment": true,
      "like": true,
      "follow": false,
      "system": true
    },
    "updatedAt": "2024-12-19T10:30:00Z"
  },
  "error": null
}
```

### 응답 필드 설명
| 필드 | 타입 | 설명 |
|------|------|------|
| `settings.comment` | boolean | 댓글 알림 활성화 여부 |
| `settings.like` | boolean | 좋아요 알림 활성화 여부 |
| `settings.follow` | boolean | 팔로우 알림 활성화 여부 |
| `settings.system` | boolean | 시스템 알림 활성화 여부 |
| `updatedAt` | string | 마지막 설정 변경 시간 |

## ✅ PUT /api/notifications/settings

### 설명
사용자의 알림 설정을 전체 업데이트합니다.

### 요청 헤더
| 헤더 | 값 | 필수 |
|------|-----|------|
| Authorization | Bearer {JWT_TOKEN} | ✅ |
| Content-Type | application/json | ✅ |

### 요청 본문
```json
{
  "settings": {
    "comment": true,
    "like": false,
    "follow": true,
    "system": true
  }
}
```

### 요청 필드 설명
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `settings.comment` | boolean | ✅ | 댓글 알림 활성화 여부 |
| `settings.like` | boolean | ✅ | 좋아요 알림 활성화 여부 |
| `settings.follow` | boolean | ✅ | 팔로우 알림 활성화 여부 |
| `settings.system` | boolean | ✅ | 시스템 알림 활성화 여부 |

### 응답 예시
```json
{
  "success": true,
  "message": "알림 설정이 업데이트되었습니다.",
  "data": {
    "settings": {
      "comment": true,
      "like": false,
      "follow": true,
      "system": true
    },
    "updatedAt": "2024-12-19T11:00:00Z"
  },
  "error": null
}
```

## ✅ PUT /api/notifications/settings/{type}

### 설명
특정 알림 타입의 설정만 업데이트합니다.

### 경로 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| type | string | ✅ | 알림 타입 (comment, like, follow, system) |

### 요청 헤더
| 헤더 | 값 | 필수 |
|------|-----|------|
| Authorization | Bearer {JWT_TOKEN} | ✅ |
| Content-Type | application/json | ✅ |

### 요청 본문
```json
{
  "enabled": false
}
```

### 요청 필드 설명
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| enabled | boolean | ✅ | 해당 알림 타입 활성화 여부 |

### 요청 예시
```
PUT /api/notifications/settings/comment
{
  "enabled": false
}
```

### 응답 예시
```json
{
  "success": true,
  "message": "댓글 알림 설정이 업데이트되었습니다.",
  "data": {
    "type": "comment",
    "enabled": false,
    "updatedAt": "2024-12-19T11:00:00Z"
  },
  "error": null
}
```

## 🔄 POST /api/notifications/settings/reset

### 설명
알림 설정을 기본값으로 초기화합니다.

### 요청 헤더
| 헤더 | 값 | 필수 |
|------|-----|------|
| Authorization | Bearer {JWT_TOKEN} | ✅ |
| Content-Type | application/json | ✅ |

### 요청 본문
없음

### 응답 예시
```json
{
  "success": true,
  "message": "알림 설정이 기본값으로 초기화되었습니다.",
  "data": {
    "settings": {
      "comment": true,
      "like": true,
      "follow": true,
      "system": true
    },
    "updatedAt": "2024-12-19T11:00:00Z"
  },
  "error": null
}
```

## ⚠️ 에러 응답

### 400 Bad Request
```json
{
  "success": false,
  "message": "잘못된 요청입니다.",
  "data": null,
  "error": {
    "code": "INVALID_SETTINGS",
    "details": "알림 설정 형식이 올바르지 않습니다."
  }
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "message": "인증이 필요합니다.",
  "data": null,
  "error": {
    "code": "UNAUTHORIZED",
    "details": "유효하지 않은 토큰입니다."
  }
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "알림 설정을 찾을 수 없습니다.",
  "data": null,
  "error": {
    "code": "SETTINGS_NOT_FOUND",
    "details": "사용자의 알림 설정이 존재하지 않습니다."
  }
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "서버 오류가 발생했습니다.",
  "data": null,
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "details": "데이터베이스 연결 오류"
  }
}
```

## 📝 알림 타입별 상세 정보

### 댓글 알림 (comment)
- **설명**: 다른 사용자가 내 게시글에 댓글을 작성하거나, 내 댓글에 대댓글을 작성할 때
- **기본값**: true
- **영향**: 댓글 관련 모든 알림

### 좋아요 알림 (like)
- **설명**: 다른 사용자가 내 게시글이나 댓글에 좋아요를 눌렀을 때
- **기본값**: true
- **영향**: 좋아요 관련 모든 알림

### 팔로우 알림 (follow)
- **설명**: 새로운 사용자가 나를 팔로우했을 때
- **기본값**: true
- **영향**: 팔로우 관련 모든 알림

### 시스템 알림 (system)
- **설명**: 공지사항, 시스템 업데이트, 관리자 메시지 등
- **기본값**: true
- **영향**: 시스템 관련 모든 알림

## 🔧 클라이언트 구현 예시

### Flutter/Dart 예시

#### 1. 알림 설정 모델
```dart
class NotificationSettings {
  final bool commentEnabled;
  final bool likeEnabled;
  final bool followEnabled;
  final bool systemEnabled;
  final DateTime updatedAt;

  NotificationSettings({
    required this.commentEnabled,
    required this.likeEnabled,
    required this.followEnabled,
    required this.systemEnabled,
    required this.updatedAt,
  });

  factory NotificationSettings.fromJson(Map<String, dynamic> json) {
    return NotificationSettings(
      commentEnabled: json['settings']['comment'] ?? true,
      likeEnabled: json['settings']['like'] ?? true,
      followEnabled: json['settings']['follow'] ?? true,
      systemEnabled: json['settings']['system'] ?? true,
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'settings': {
        'comment': commentEnabled,
        'like': likeEnabled,
        'follow': followEnabled,
        'system': systemEnabled,
      },
    };
  }
}
```

#### 2. 알림 설정 서비스
```dart
class NotificationSettingsService {
  static const String baseUrl = 'http://localhost:8080/api';
  final String token;

  NotificationSettingsService(this.token);

  // 알림 설정 조회
  Future<NotificationSettings> getSettings() async {
    final response = await http.get(
      Uri.parse('$baseUrl/notifications/settings'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return NotificationSettings.fromJson(data['data']);
    } else {
      throw Exception('알림 설정 조회 실패');
    }
  }

  // 알림 설정 전체 업데이트
  Future<NotificationSettings> updateSettings(NotificationSettings settings) async {
    final response = await http.put(
      Uri.parse('$baseUrl/notifications/settings'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: json.encode(settings.toJson()),
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return NotificationSettings.fromJson(data['data']);
    } else {
      throw Exception('알림 설정 업데이트 실패');
    }
  }

  // 특정 알림 타입 설정 업데이트
  Future<Map<String, dynamic>> updateTypeSetting(String type, bool enabled) async {
    final response = await http.put(
      Uri.parse('$baseUrl/notifications/settings/$type'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: json.encode({'enabled': enabled}),
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['data'];
    } else {
      throw Exception('알림 타입 설정 업데이트 실패');
    }
  }

  // 알림 설정 초기화
  Future<NotificationSettings> resetSettings() async {
    final response = await http.post(
      Uri.parse('$baseUrl/notifications/settings/reset'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return NotificationSettings.fromJson(data['data']);
    } else {
      throw Exception('알림 설정 초기화 실패');
    }
  }
}
```

#### 3. 알림 설정 Provider
```dart
class NotificationSettingsProvider extends ChangeNotifier {
  NotificationSettings? _settings;
  NotificationSettingsService? _service;
  bool _isLoading = false;

  NotificationSettings? get settings => _settings;
  bool get isLoading => _isLoading;

  void initialize(String token) {
    _service = NotificationSettingsService(token);
    loadSettings();
  }

  Future<void> loadSettings() async {
    if (_service == null) return;

    _isLoading = true;
    notifyListeners();

    try {
      _settings = await _service!.getSettings();
    } catch (e) {
      print('알림 설정 로드 실패: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> updateSettings(NotificationSettings newSettings) async {
    if (_service == null) return;

    try {
      _settings = await _service!.updateSettings(newSettings);
      notifyListeners();
    } catch (e) {
      print('알림 설정 업데이트 실패: $e');
      rethrow;
    }
  }

  Future<void> updateTypeSetting(String type, bool enabled) async {
    if (_service == null) return;

    try {
      await _service!.updateTypeSetting(type, enabled);
      await loadSettings(); // 설정 다시 로드
    } catch (e) {
      print('알림 타입 설정 업데이트 실패: $e');
      rethrow;
    }
  }

  Future<void> resetSettings() async {
    if (_service == null) return;

    try {
      _settings = await _service!.resetSettings();
      notifyListeners();
    } catch (e) {
      print('알림 설정 초기화 실패: $e');
      rethrow;
    }
  }
}
```

#### 4. 알림 설정 UI 위젯
```dart
class NotificationSettingsScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('알림 설정'),
        actions: [
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: () {
              context.read<NotificationSettingsProvider>().resetSettings();
            },
          ),
        ],
      ),
      body: Consumer<NotificationSettingsProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading) {
            return Center(child: CircularProgressIndicator());
          }

          final settings = provider.settings;
          if (settings == null) {
            return Center(child: Text('설정을 불러올 수 없습니다.'));
          }

          return ListView(
            children: [
              _buildSettingTile(
                context,
                '댓글 알림',
                '다른 사용자의 댓글 알림을 받습니다.',
                settings.commentEnabled,
                (value) => provider.updateTypeSetting('comment', value),
              ),
              _buildSettingTile(
                context,
                '좋아요 알림',
                '다른 사용자의 좋아요 알림을 받습니다.',
                settings.likeEnabled,
                (value) => provider.updateTypeSetting('like', value),
              ),
              _buildSettingTile(
                context,
                '팔로우 알림',
                '새로운 팔로워 알림을 받습니다.',
                settings.followEnabled,
                (value) => provider.updateTypeSetting('follow', value),
              ),
              _buildSettingTile(
                context,
                '시스템 알림',
                '공지사항 및 시스템 메시지를 받습니다.',
                settings.systemEnabled,
                (value) => provider.updateTypeSetting('system', value),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildSettingTile(
    BuildContext context,
    String title,
    String subtitle,
    bool value,
    Function(bool) onChanged,
  ) {
    return SwitchListTile(
      title: Text(title),
      subtitle: Text(subtitle),
      value: value,
      onChanged: onChanged,
    );
  }
}
```

## 🧪 API 테스트

### cURL 테스트 명령어

#### 1. 알림 설정 조회
```bash
curl -X GET "http://localhost:8080/api/notifications/settings" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### 2. 알림 설정 전체 업데이트
```bash
curl -X PUT "http://localhost:8080/api/notifications/settings" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "settings": {
      "comment": true,
      "like": false,
      "follow": true,
      "system": true
    }
  }'
```

#### 3. 특정 알림 타입 설정 업데이트
```bash
curl -X PUT "http://localhost:8080/api/notifications/settings/comment" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"enabled": false}'
```

#### 4. 알림 설정 초기화
```bash
curl -X POST "http://localhost:8080/api/notifications/settings/reset" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 테스트 스크립트 사용
```bash
# JWT 토큰으로 전체 API 테스트
./TEST_NOTIFICATION_SETTINGS_API.sh "your-jwt-token"
```

## 📋 구현 완료 사항

### ✅ 백엔드 구현
- [x] NotificationSettings 엔티티
- [x] NotificationSettingsRepository
- [x] NotificationSettingsService
- [x] NotificationSettingsController
- [x] NotificationSettingsDto
- [x] 데이터베이스 테이블 생성
- [x] 기존 알림 서비스와 연동

### ✅ API 엔드포인트
- [x] GET /api/notifications/settings
- [x] PUT /api/notifications/settings
- [x] PUT /api/notifications/settings/{type}
- [x] POST /api/notifications/settings/reset

### ✅ 기능
- [x] 알림 설정 조회/업데이트/초기화
- [x] 알림 타입별 개별 설정
- [x] 기존 알림 발송 시 설정 확인
- [x] 기본값 자동 생성
- [x] 유효성 검사 및 에러 처리
- [x] 일관된 응답 형식

### ✅ 테스트
- [x] 빌드 테스트 통과
- [x] API 테스트 스크립트 생성
- [x] cURL 테스트 명령어 제공

## 🚀 사용 시작

1. **서버 실행**
   ```bash
   ./run-local.sh
   ```

2. **JWT 토큰 획득** (로그인 API 사용)

3. **API 테스트**
   ```bash
   ./TEST_NOTIFICATION_SETTINGS_API.sh "your-jwt-token"
   ```

4. **클라이언트 연동** (위의 Flutter 예시 코드 사용)

---

**📞 문의사항**: API 사용 중 문제가 발생하면 서버 로그를 확인하거나 개발팀에 문의하세요. 