# 좋아요 API 현재 상황 및 해결 방안

## 🚨 현재 문제 상황

### 클라이언트에서 발생하는 에러
1. **`/api/posts/{id}/like` (POST)**: 500 에러 (NoResourceFoundException)
2. **`/api/posts/{id}/toggle-like` (POST)**: 404 에러 (클라이언트에서 아직 사용하지 않음)

### 서버 상태 확인 결과
- ✅ **`/toggle-like` 엔드포인트**: 정상 등록 및 작동
- ❌ **`/like` 엔드포인트**: 주석 처리되어 있음 (의도적 비활성화)

## 🔧 해결 방안

### 1. 권장 해결책: 클라이언트에서 `/toggle-like` 사용

**변경 사항:**
```dart
// 기존 (에러 발생)
POST /api/posts/{id}/like

// 변경 (정상 작동)
POST /api/posts/{id}/toggle-like
```

**장점:**
- 서버 수정 불필요
- 토글 방식으로 더 효율적
- 하나의 엔드포인트로 추가/취소 처리

### 2. 대안: 서버에서 `/like` 엔드포인트 재활성화

만약 클라이언트 수정이 어려운 경우, 서버에서 기존 엔드포인트를 다시 활성화할 수 있습니다.

## 📋 클라이언트 수정 가이드

### 1. API 호출 부분 수정

```dart
// 기존 코드
Future<void> likePost(int postId) async {
  final response = await http.post(
    Uri.parse('$baseUrl/api/posts/$postId/like'), // ❌ 에러 발생
    headers: headers,
  );
  // ...
}

// 수정된 코드
Future<void> togglePostLike(int postId) async {
  final response = await http.post(
    Uri.parse('$baseUrl/api/posts/$postId/toggle-like'), // ✅ 정상 작동
    headers: headers,
  );
  // ...
}
```

### 2. 응답 처리 수정

```dart
// 기존 응답 형식
{
  "message": "좋아요가 추가되었습니다.",
  "likeCount": 5,
  "isLiked": true
}

// 새로운 응답 형식 (동일함)
{
  "message": "좋아요가 추가되었습니다.",
  "count": 5,
  "liked": true,
  "reported": false,
  "bookmarked": false
}
```

### 3. UI 상태 관리

```dart
class PostDetailPage extends StatefulWidget {
  @override
  _PostDetailPageState createState() => _PostDetailPageState();
}

class _PostDetailPageState extends State<PostDetailPage> {
  bool isLiked = false;
  int likeCount = 0;
  
  // 좋아요 토글 함수
  Future<void> _toggleLike() async {
    try {
      final result = await ApiService.togglePostLike(widget.postId);
      setState(() {
        isLiked = result['liked']; // 'liked' 필드 사용
        likeCount = result['count']; // 'count' 필드 사용
      });
      
      // 사용자 피드백
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(result['message'])),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('좋아요 처리 중 오류가 발생했습니다.')),
      );
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          // 좋아요 버튼
          Row(
            children: [
              IconButton(
                icon: Icon(
                  isLiked ? Icons.favorite : Icons.favorite_border,
                  color: isLiked ? Colors.red : Colors.grey,
                ),
                onPressed: _toggleLike, // 토글 함수 호출
              ),
              Text('$likeCount'),
            ],
          ),
        ],
      ),
    );
  }
}
```

## 🧪 테스트 방법

### 1. curl로 API 테스트

```bash
# 토글 API 테스트 (정상 작동)
curl -X POST http://localhost:8080/api/posts/24/toggle-like \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 기존 API 테스트 (에러 발생)
curl -X POST http://localhost:8080/api/posts/24/like \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Flutter에서 테스트

```dart
// 테스트 코드
void testToggleLike() async {
  try {
    final result = await ApiService.togglePostLike(24);
    print('토글 성공: ${result['message']}');
    print('좋아요 수: ${result['count']}');
    print('좋아요 상태: ${result['liked']}');
  } catch (e) {
    print('토글 실패: $e');
  }
}
```

## 📝 구현 체크리스트

### 클라이언트 수정 사항
- [ ] API 엔드포인트를 `/toggle-like`로 변경
- [ ] 응답 필드명 변경 (`likeCount` → `count`, `isLiked` → `liked`)
- [ ] UI 상태 업데이트 로직 수정
- [ ] 에러 처리 및 사용자 피드백 구현
- [ ] JWT 토큰 인증 확인

### 테스트 사항
- [ ] 좋아요 추가 기능 테스트
- [ ] 좋아요 취소 기능 테스트
- [ ] 좋아요 수 표시 테스트
- [ ] UI 상태 동기화 테스트
- [ ] 에러 상황 처리 테스트

## 🚀 추가 정보

### 서버 API 명세서
- **파일**: `flutter_api_endpoints.txt`
- **내용**: 완전한 API 명세서와 Flutter 연동 예시 코드

### 서버 상태
- **서버 실행**: ✅ 정상 실행 중 (포트 8080)
- **CORS 설정**: ✅ PATCH 메서드 포함
- **네트워크 바인딩**: ✅ 0.0.0.0 (Flutter 접근 가능)

### 연락처
- **서버 개발자**: AI Assistant
- **문의사항**: API 연동 관련 문제 발생 시 즉시 문의

---

**결론**: 클라이언트에서 `/toggle-like` 엔드포인트를 사용하도록 수정하면 모든 문제가 해결됩니다. 서버는 정상 작동 중이며, 토글 방식이 더 효율적이고 안정적입니다. 