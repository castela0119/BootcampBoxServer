# Flutter 클라이언트 API 변경사항 가이드

## 📋 개요
서버에서 게시글 관련 API의 보안을 강화하기 위해 일부 엔드포인트의 요청 방식이 변경되었습니다. 플러터 클라이언트에서 반드시 수정해야 할 부분들을 안내합니다.

---

## 🔐 인증 관련 변경사항

### 1. JWT 토큰 헤더 추가
모든 인증이 필요한 API 호출 시 JWT 토큰을 헤더에 포함해야 합니다.

```dart
// 기존 방식 (❌ 더 이상 사용 불가)
final response = await http.post(
  Uri.parse('$baseUrl/api/posts?userId=$userId'),
  headers: {'Content-Type': 'application/json'},
  body: jsonEncode(requestBody),
);

// 새로운 방식 (✅ 올바른 방식)
final response = await http.post(
  Uri.parse('$baseUrl/api/posts'),
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer $jwtToken', // JWT 토큰 추가
  },
  body: jsonEncode(requestBody),
);
```

---

## 🚨 수정이 필요한 API 엔드포인트

### 1. 게시글 작성 API

#### 변경 전 (❌)
```dart
// URL에 userId 파라미터 포함
POST /api/posts?userId={userId}
Content-Type: application/json

{
  "title": "게시글 제목",
  "content": "게시글 내용",
  "tagNames": ["태그1", "태그2"],
  "isAnonymous": false,
  "authorUserType": "soldier"
}
```

#### 변경 후 (✅)
```dart
// URL에서 userId 파라미터 제거, JWT 토큰으로 사용자 식별
POST /api/posts
Content-Type: application/json
Authorization: Bearer {jwtToken}

{
  "title": "게시글 제목",
  "content": "게시글 내용",
  "tagNames": ["태그1", "태그2"],
  "isAnonymous": false,
  "authorUserType": "soldier"
}
```

#### Flutter 코드 수정 예시
```dart
class PostService {
  static const String baseUrl = 'http://your-server-url:8080';
  
  // 기존 코드 (수정 필요)
  static Future<PostResponse> createPost({
    required int userId,  // ❌ 제거
    required String title,
    required String content,
    List<String>? tagNames,
    bool isAnonymous = false,
    String? authorUserType,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/posts?userId=$userId'), // ❌ 수정 필요
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'title': title,
        'content': content,
        'tagNames': tagNames,
        'isAnonymous': isAnonymous,
        'authorUserType': authorUserType,
      }),
    );
    // ...
  }
  
  // 수정된 코드 (✅ 올바른 방식)
  static Future<PostResponse> createPost({
    required String jwtToken,  // ✅ JWT 토큰 추가
    required String title,
    required String content,
    List<String>? tagNames,
    bool isAnonymous = false,
    String? authorUserType,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/posts'), // ✅ URL에서 userId 제거
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken', // ✅ JWT 토큰 헤더 추가
      },
      body: jsonEncode({
        'title': title,
        'content': content,
        'tagNames': tagNames,
        'isAnonymous': isAnonymous,
        'authorUserType': authorUserType,
      }),
    );
    // ...
  }
}
```

### 2. 게시글 수정 API

#### 변경 전 (❌)
```dart
PUT /api/posts/{postId}?userId={userId}
Content-Type: application/json

{
  "title": "수정된 제목",
  "content": "수정된 내용",
  "tagNames": ["새태그1", "새태그2"],
  "isAnonymous": false,
  "authorUserType": "soldier"
}
```

#### 변경 후 (✅)
```dart
PUT /api/posts/{postId}
Content-Type: application/json
Authorization: Bearer {jwtToken}

{
  "title": "수정된 제목",
  "content": "수정된 내용",
  "tagNames": ["새태그1", "새태그2"],
  "isAnonymous": false,
  "authorUserType": "soldier"
}
```

#### Flutter 코드 수정 예시
```dart
// 기존 코드 (수정 필요)
static Future<PostResponse> updatePost({
  required int postId,
  required int userId,  // ❌ 제거
  required String title,
  required String content,
  List<String>? tagNames,
  bool isAnonymous = false,
  String? authorUserType,
}) async {
  final response = await http.put(
    Uri.parse('$baseUrl/api/posts/$postId?userId=$userId'), // ❌ 수정 필요
    headers: {'Content-Type': 'application/json'},
    body: jsonEncode({
      'title': title,
      'content': content,
      'tagNames': tagNames,
      'isAnonymous': isAnonymous,
      'authorUserType': authorUserType,
    }),
  );
  // ...
}

// 수정된 코드 (✅ 올바른 방식)
static Future<PostResponse> updatePost({
  required int postId,
  required String jwtToken,  // ✅ JWT 토큰 추가
  required String title,
  required String content,
  List<String>? tagNames,
  bool isAnonymous = false,
  String? authorUserType,
}) async {
  final response = await http.put(
    Uri.parse('$baseUrl/api/posts/$postId'), // ✅ URL에서 userId 제거
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $jwtToken', // ✅ JWT 토큰 헤더 추가
    },
    body: jsonEncode({
      'title': title,
      'content': content,
      'tagNames': tagNames,
      'isAnonymous': isAnonymous,
      'authorUserType': authorUserType,
    }),
  );
  // ...
}
```

### 3. 게시글 삭제 API

#### 변경 전 (❌)
```dart
DELETE /api/posts/{postId}?userId={userId}
```

#### 변경 후 (✅)
```dart
DELETE /api/posts/{postId}
Authorization: Bearer {jwtToken}
```

#### Flutter 코드 수정 예시
```dart
// 기존 코드 (수정 필요)
static Future<void> deletePost({
  required int postId,
  required int userId,  // ❌ 제거
}) async {
  final response = await http.delete(
    Uri.parse('$baseUrl/api/posts/$postId?userId=$userId'), // ❌ 수정 필요
  );
  // ...
}

// 수정된 코드 (✅ 올바른 방식)
static Future<void> deletePost({
  required int postId,
  required String jwtToken,  // ✅ JWT 토큰 추가
}) async {
  final response = await http.delete(
    Uri.parse('$baseUrl/api/posts/$postId'), // ✅ URL에서 userId 제거
    headers: {
      'Authorization': 'Bearer $jwtToken', // ✅ JWT 토큰 헤더 추가
    },
  );
  // ...
}
```

---

## 🔄 변경이 필요하지 않은 API들

다음 API들은 기존 방식 그대로 사용하면 됩니다:

### 1. 게시글 조회 API들
```dart
// 변경 없음 - 그대로 사용
GET /api/posts                    // 게시글 목록 조회
GET /api/posts/{postId}           // 게시글 상세 조회
GET /api/posts/{postId}/comments  // 댓글 목록 조회
```

### 2. 댓글 관련 API들
```dart
// 변경 없음 - 그대로 사용 (단, JWT 토큰은 필요)
POST /api/posts/{postId}/comments           // 댓글 작성
PATCH /api/posts/comments/{commentId}       // 댓글 수정
DELETE /api/posts/comments/{commentId}      // 댓글 삭제
```

### 3. 좋아요/신고/북마크 API들
```dart
// 변경 없음 - 그대로 사용 (단, JWT 토큰은 필요)
POST /api/posts/{postId}/toggle-like        // 게시글 좋아요 토글
POST /api/posts/{postId}/report             // 게시글 신고
POST /api/posts/{postId}/bookmark           // 게시글 북마크
POST /api/posts/comments/{commentId}/toggle-like  // 댓글 좋아요 토글
POST /api/posts/comments/{commentId}/report       // 댓글 신고
```

---

## 🛠️ Flutter 코드 수정 가이드

### 1. HTTP 클라이언트 헬퍼 함수 생성
```dart
class ApiHelper {
  static const String baseUrl = 'http://your-server-url:8080';
  
  // JWT 토큰을 포함한 헤더 생성
  static Map<String, String> getAuthHeaders(String jwtToken) {
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $jwtToken',
    };
  }
  
  // 기본 헤더 (인증 불필요한 API용)
  static Map<String, String> getBasicHeaders() {
    return {
      'Content-Type': 'application/json',
    };
  }
}
```

### 2. 서비스 클래스 수정 예시
```dart
class PostService {
  // 게시글 작성 (수정됨)
  static Future<PostResponse> createPost({
    required String jwtToken,
    required String title,
    required String content,
    List<String>? tagNames,
    bool isAnonymous = false,
    String? authorUserType,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('${ApiHelper.baseUrl}/api/posts'),
        headers: ApiHelper.getAuthHeaders(jwtToken),
        body: jsonEncode({
          'title': title,
          'content': content,
          'tagNames': tagNames,
          'isAnonymous': isAnonymous,
          'authorUserType': authorUserType,
        }),
      );
      
      if (response.statusCode == 200) {
        return PostResponse.fromJson(jsonDecode(response.body));
      } else {
        throw Exception('게시글 작성 실패: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('게시글 작성 중 오류 발생: $e');
    }
  }
  
  // 게시글 수정 (수정됨)
  static Future<PostResponse> updatePost({
    required int postId,
    required String jwtToken,
    required String title,
    required String content,
    List<String>? tagNames,
    bool isAnonymous = false,
    String? authorUserType,
  }) async {
    try {
      final response = await http.put(
        Uri.parse('${ApiHelper.baseUrl}/api/posts/$postId'),
        headers: ApiHelper.getAuthHeaders(jwtToken),
        body: jsonEncode({
          'title': title,
          'content': content,
          'tagNames': tagNames,
          'isAnonymous': isAnonymous,
          'authorUserType': authorUserType,
        }),
      );
      
      if (response.statusCode == 200) {
        return PostResponse.fromJson(jsonDecode(response.body));
      } else {
        throw Exception('게시글 수정 실패: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('게시글 수정 중 오류 발생: $e');
    }
  }
  
  // 게시글 삭제 (수정됨)
  static Future<void> deletePost({
    required int postId,
    required String jwtToken,
  }) async {
    try {
      final response = await http.delete(
        Uri.parse('${ApiHelper.baseUrl}/api/posts/$postId'),
        headers: ApiHelper.getAuthHeaders(jwtToken),
      );
      
      if (response.statusCode != 200) {
        throw Exception('게시글 삭제 실패: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('게시글 삭제 중 오류 발생: $e');
    }
  }
  
  // 게시글 목록 조회 (변경 없음)
  static Future<PostListResponse> getPosts({
    int page = 0,
    int size = 10,
    String? search,
    String? authorUserType,
    String? tags,
    String? sortBy,
    String? sortOrder,
  }) async {
    try {
      final queryParams = <String, String>{
        'page': page.toString(),
        'size': size.toString(),
      };
      
      if (search != null) queryParams['search'] = search;
      if (authorUserType != null) queryParams['authorUserType'] = authorUserType;
      if (tags != null) queryParams['tags'] = tags;
      if (sortBy != null) queryParams['sortBy'] = sortBy;
      if (sortOrder != null) queryParams['sortOrder'] = sortOrder;
      
      final uri = Uri.parse('${ApiHelper.baseUrl}/api/posts')
          .replace(queryParameters: queryParams);
      
      final response = await http.get(
        uri,
        headers: ApiHelper.getBasicHeaders(),
      );
      
      if (response.statusCode == 200) {
        return PostListResponse.fromJson(jsonDecode(response.body));
      } else {
        throw Exception('게시글 목록 조회 실패: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('게시글 목록 조회 중 오류 발생: $e');
    }
  }
}
```

### 3. UI에서 사용하는 방법
```dart
class PostCreateScreen extends StatefulWidget {
  @override
  _PostCreateScreenState createState() => _PostCreateScreenState();
}

class _PostCreateScreenState extends State<PostCreateScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _contentController = TextEditingController();
  
  Future<void> _createPost() async {
    if (!_formKey.currentState!.validate()) return;
    
    try {
      // JWT 토큰은 로그인 후 저장된 토큰 사용
      final jwtToken = await getStoredJwtToken(); // 토큰 저장소에서 가져오기
      
      final response = await PostService.createPost(
        jwtToken: jwtToken,
        title: _titleController.text,
        content: _contentController.text,
        tagNames: ['태그1', '태그2'], // 선택적
        isAnonymous: false,
        authorUserType: 'soldier',
      );
      
      // 성공 처리
      Navigator.pop(context, response);
    } catch (e) {
      // 오류 처리
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('게시글 작성 실패: $e')),
      );
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('게시글 작성')),
      body: Form(
        key: _formKey,
        child: Column(
          children: [
            TextFormField(
              controller: _titleController,
              decoration: InputDecoration(labelText: '제목'),
              validator: (value) {
                if (value?.isEmpty ?? true) return '제목을 입력해주세요';
                return null;
              },
            ),
            TextFormField(
              controller: _contentController,
              decoration: InputDecoration(labelText: '내용'),
              maxLines: 5,
              validator: (value) {
                if (value?.isEmpty ?? true) return '내용을 입력해주세요';
                return null;
              },
            ),
            ElevatedButton(
              onPressed: _createPost,
              child: Text('게시글 작성'),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## 🔑 JWT 토큰 관리

### 1. 토큰 저장소 구현
```dart
class TokenStorage {
  static const String _tokenKey = 'jwt_token';
  
  // 토큰 저장
  static Future<void> saveToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_tokenKey, token);
  }
  
  // 토큰 가져오기
  static Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_tokenKey);
  }
  
  // 토큰 삭제 (로그아웃 시)
  static Future<void> deleteToken() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_tokenKey);
  }
}
```

### 2. 로그인 후 토큰 저장
```dart
class AuthService {
  static Future<void> login({
    required String username,
    required String password,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('${ApiHelper.baseUrl}/api/auth/login'),
        headers: ApiHelper.getBasicHeaders(),
        body: jsonEncode({
          'username': username,
          'password': password,
        }),
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final token = data['token']; // JWT 토큰
        
        // 토큰 저장
        await TokenStorage.saveToken(token);
      } else {
        throw Exception('로그인 실패');
      }
    } catch (e) {
      throw Exception('로그인 중 오류 발생: $e');
    }
  }
}
```

---

## 🔄 댓글 페이징 문제 해결 가이드

### 🚨 발견된 문제점 (클라이언트 로그 분석)

```
🔄 새로고침: 기존 댓글 목록 교체
✅ 댓글 목록 업데이트 완료:
  - 이전 페이지: 0 → 현재 페이지: 1  ← 문제!
```

**문제 원인:**
1. **첫 번째 요청이 새로고침 모드로 실행됨** (`refresh=true`)
2. **페이지 번호가 잘못 증가됨** (0 → 1)
3. **두 번째 요청부터 페이지 2를 요청** (데이터 없음)

### 🚨 추가 발견된 문제점 (최신 로그 분석)

```
📥 추가 로드: 기존 댓글에 새 댓글 추가
✅ 댓글 목록 업데이트 완료:
  - 이전 페이지: 0 → 현재 페이지: 1  ← 여전히 문제!
```

**새로운 문제 원인:**
1. **페이지 번호 증가 로직이 잘못됨** - 서버 응답의 `currentPage`를 그대로 사용하지 않고 클라이언트에서 증가시킴
2. **첫 번째 요청에서 이미 페이지가 1로 설정됨** - 두 번째 요청은 페이지 2를 요청

### 🔧 구체적인 해결 방법

#### 1. 페이지 번호 증가 로직 수정 (가장 중요!)

```dart
// ❌ 잘못된 방법 (현재 클라이언트 코드)
state.currentPage = commentListResponse.currentPage + 1; // 이렇게 하면 안됨!

// ✅ 올바른 방법
if (refresh) {
  // 새로고침 모드: 페이지를 0으로 초기화
  state.currentPage = 0;
} else {
  // 추가 로딩 모드: 현재 페이지 + 1
  state.currentPage = commentListResponse.currentPage + 1;
}
```

#### 2. 완전히 수정된 댓글 로딩 로직

```dart
Future<void> loadComments(int postId, {bool refresh = false}) async {
  print('=== 댓글 로딩 시작 ===');
  print('새로고침 모드: $refresh');
  print('현재 페이지: ${state.currentPage}');
  print('로딩 중: ${state.isLoading}');
  print('다음 페이지 있음: ${state.hasNext}');
  
  if (refresh) {
    print('🔄 새로고침 모드: 상태 초기화');
    state.reset();
  }
  
  // 이미 로딩 중이거나 더 이상 데이터가 없으면 중단
  if (state.isLoading || !state.hasNext) {
    print('❌ 로딩 중단: isLoading=${state.isLoading}, hasNext=${state.hasNext}');
    return;
  }
  
  try {
    state.isLoading = true;
    
    print('📡 API 요청: page=${state.currentPage}, size=10');
    final response = await dio.get(
      '/api/posts/$postId/comments',
      queryParameters: {
        'page': state.currentPage,
        'size': 10,
      },
    );
    
    final commentListResponse = CommentListResponse.fromJson(response.data);
    
    print('📊 서버 응답 분석:');
    print('  - 새로 받은 댓글: ${commentListResponse.comments.length}개');
    print('  - 총 댓글 수: ${commentListResponse.totalComments}개');
    print('  - 서버 응답 현재 페이지: ${commentListResponse.currentPage}');
    print('  - 총 페이지: ${commentListResponse.totalPages}');
    print('  - 다음 페이지 있음: ${commentListResponse.hasNext}');
    
    if (refresh) {
      print('🔄 새로고침: 기존 댓글 목록 교체');
      state.comments = commentListResponse.comments;
    } else {
      print('➕ 추가 로딩: 기존 댓글에 추가');
      state.comments.addAll(commentListResponse.comments);
    }
    
    // 중요: 서버 응답의 hasNext 값을 사용
    state.hasNext = commentListResponse.hasNext;
    
    // 중요: 페이지 번호 증가 로직 수정
    if (refresh) {
      // 새로고침 모드: 다음 페이지는 1
      state.currentPage = 1;
      print('🔄 새로고침 후 다음 페이지: 1');
    } else {
      // 추가 로딩 모드: 현재 페이지 + 1
      state.currentPage = commentListResponse.currentPage + 1;
      print('➕ 추가 로딩 후 다음 페이지: ${state.currentPage}');
    }
    
    print('✅ 댓글 목록 업데이트 완료:');
    print('  - 현재 댓글 수: ${state.comments.length}개');
    print('  - 총 댓글 수: ${commentListResponse.totalComments}개');
    print('  - 다음 요청 페이지: ${state.currentPage}');
    print('  - 더 불러올 수 있음: ${state.hasNext}');
    
  } catch (e) {
    print('❌ 댓글 로딩 오류: $e');
  } finally {
    state.isLoading = false;
    print('🏁 댓글 로딩 완료: isLoading=${state.isLoading}');
  }
}
```

#### 3. 새 댓글 작성 후 올바른 새로고침

```dart
Future<void> createComment(int postId, String content) async {
  try {
    print('📝 댓글 작성 시작');
    await dio.post(
      '/api/posts/$postId/comments',
      data: {'content': content},
    );
    
    print('✅ 댓글 작성 성공, 목록 새로고침');
    // 댓글 작성 성공 후 목록 새로고침
    await loadComments(postId, refresh: true);
    
  } catch (e) {
    print('❌ 댓글 작성 오류: $e');
  }
}
```

#### 4. 상태 초기화 수정

```dart
class CommentListState {
  List<Comment> comments = [];
  int currentPage = 0; // 초기값 0
  bool isLoading = false;
  bool hasNext = true; // 초기값 true
  
  void reset() {
    print('🔄 상태 초기화');
    comments.clear();
    currentPage = 0; // 0으로 초기화 (중요!)
    isLoading = false;
    hasNext = true; // true로 초기화
    print('  - currentPage: $currentPage');
    print('  - hasNext: $hasNext');
  }
}
```

#### 5. 무한 스크롤 트리거 수정

```dart
// 스크롤이 끝에 도달했을 때
void _onScrollEnd() {
  print('📜 스크롤 끝 도달');
  print('  - 현재 댓글 수: ${state.comments.length}');
  print('  - 로딩 중: ${state.isLoading}');
  print('  - 다음 페이지 있음: ${state.hasNext}');
  
  if (!state.isLoading && state.hasNext) {
    print('🔄 추가 댓글 로딩 시작');
    loadComments(postId, refresh: false); // refresh=false로 추가 로딩
  } else {
    print('❌ 추가 로딩 불가: isLoading=${state.isLoading}, hasNext=${state.hasNext}');
  }
}
```

### 🧪 테스트 시나리오

**정상적인 동작 순서:**
1. **초기 로딩**: `page=0` → 댓글 1~10번 (10개)
2. **스크롤 끝**: `page=1` → 댓글 11~20번 (10개)
3. **더 이상 없음**: `hasNext=false` → 로딩 중단

**현재 잘못된 동작:**
1. **초기 로딩**: `page=0` → 댓글 1~10번 (10개) ✅
2. **페이지 증가**: `currentPage=1` ❌
3. **다음 요청**: `page=1` → 댓글 11~20번 (10개) ✅
4. **페이지 증가**: `currentPage=2` ❌
5. **다음 요청**: `page=2` → 데이터 없음 ❌

### ✅ 확인 체크리스트

- [ ] 초기 로딩 시 `refresh=false` 사용
- [ ] 새 댓글 작성 후에만 `refresh=true` 사용
- [ ] 서버 응답의 `hasNext` 값을 그대로 사용
- [ ] 페이지 번호를 `currentPage + 1`로 증가
- [ ] 로딩 중이거나 `hasNext=false`일 때 추가 요청 중단

---

## ⚠️ 주의사항

### 1. 보안 관련
- JWT 토큰은 민감한 정보이므로 안전하게 저장해야 합니다
- 토큰 만료 시 자동으로 로그아웃 처리해야 합니다
- HTTPS 사용을 권장합니다

### 2. 오류 처리
- 네트워크 오류, 인증 오류, 서버 오류를 구분하여 처리해야 합니다
- 사용자에게 적절한 오류 메시지를 표시해야 합니다

### 3. 테스트
- 수정 후 모든 API 호출을 테스트해야 합니다
- 토큰 만료 시나리오도 테스트해야 합니다

---

## 📝 체크리스트

- [ ] 게시글 작성 API 수정 (userId 파라미터 제거, JWT 토큰 추가)
- [ ] 게시글 수정 API 수정 (userId 파라미터 제거, JWT 토큰 추가)
- [ ] 게시글 삭제 API 수정 (userId 파라미터 제거, JWT 토큰 추가)
- [ ] JWT 토큰 저장소 구현
- [ ] 로그인 후 토큰 저장 로직 구현
- [ ] 모든 API 호출에서 적절한 헤더 사용
- [ ] 오류 처리 로직 구현
- [ ] 전체 기능 테스트 완료

---

## 🎯 결론

이 변경사항들은 **보안 강화**를 위한 것으로, 플러터 클라이언트에서 반드시 수정해야 합니다. 수정 후에는 더 안전하고 표준적인 API 호출 방식이 됩니다.

주요 변경점:
1. **URL 파라미터에서 userId 제거**
2. **JWT 토큰을 Authorization 헤더에 추가**
3. **적절한 오류 처리 구현**

이 가이드를 따라 수정하면 서버와 정상적으로 연동될 것입니다! 🚀 