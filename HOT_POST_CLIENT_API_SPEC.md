# 🔥 HOT 게시글 API 명세서 (클라이언트용)

## 📋 개요

HOT 게시글 시스템은 사용자 참여도와 시간 가중치를 기반으로 인기 게시글을 자동 선정하는 시스템입니다. 클라이언트에서는 이 API를 통해 인기 게시글을 조회하고 관리할 수 있습니다.

### 🎯 HOT 점수 계산 공식
```
HOT 점수 = (좋아요 × 3) + (댓글수 × 2) + 시간가중치
```

### ⏰ 시간 가중치 기준
- **오늘 작성**: +50점
- **어제 작성**: +30점  
- **2일 전 작성**: +20점
- **3일 전 작성**: +10점
- **4-5일 전 작성**: +5점
- **6일 이후**: +0점

### 🏆 HOT 게시글 선정 기준
- **총점 80점 이상**인 게시글이 HOT 게시글로 선정
- **총점 80점 미만**으로 떨어지면 HOT 게시글에서 제외

---

## 🔗 API 엔드포인트 목록

### 1. 일반 사용자 API
- **GET** `/api/posts/hot` - HOT 게시글 목록 조회

### 2. 관리자 전용 API
- **GET** `/api/admin/hot-stats` - HOT 게시글 통계 조회
- **POST** `/api/admin/posts/{id}/hot-score` - HOT 점수 수동 조정

---

## 📝 API 상세 명세

### 1. HOT 게시글 목록 조회

#### 엔드포인트
```
GET /api/posts/hot
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| `page` | Integer | ❌ | 0 | 페이지 번호 (0부터 시작) |
| `size` | Integer | ❌ | 10 | 페이지당 게시글 수 |
| `period` | Integer | ❌ | 0 | 조회 기간 (일, 0: 전체) |

#### 요청 예시
```bash
# 전체 HOT 게시글 조회
curl -X GET "http://localhost:8080/api/posts/hot?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 최근 7일 내 HOT 게시글 조회
curl -X GET "http://localhost:8080/api/posts/hot?page=0&size=10&period=7" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 최근 3일 내 HOT 게시글 조회
curl -X GET "http://localhost:8080/api/posts/hot?page=0&size=10&period=3" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 응답 예시
```json
{
  "content": [
    {
      "id": 46,
      "title": "인기 게시글 제목",
      "content": "게시글 내용...",
      "authorNickname": "작성자",
      "authorUsername": "author@example.com",
      "authorId": 14,
      "isAnonymous": false,
      "anonymousNickname": null,
      "authorUserType": "STUDENT",
      "createdAt": "2025-07-19T10:00:00",
      "updatedAt": "2025-07-19T10:00:00",
      "likeCount": 25,
      "commentCount": 12,
      "viewCount": 150,
      "hotScore": 125,
      "isHot": true,
      "hotUpdatedAt": "2025-07-19T15:30:00",
      "tagNames": ["인기", "추천"]
    },
    {
      "id": 47,
      "title": "두 번째 인기 게시글",
      "content": "두 번째 게시글 내용...",
      "authorNickname": "다른작성자",
      "authorUsername": "other@example.com",
      "authorId": 15,
      "isAnonymous": false,
      "anonymousNickname": null,
      "authorUserType": "GRADUATE",
      "createdAt": "2025-07-19T09:00:00",
      "updatedAt": "2025-07-19T09:00:00",
      "likeCount": 18,
      "commentCount": 8,
      "viewCount": 120,
      "hotScore": 98,
      "isHot": true,
      "hotUpdatedAt": "2025-07-19T14:45:00",
      "tagNames": ["질문", "답변"]
    }
  ],
  "totalElements": 5,
  "currentPage": 0,
  "totalPages": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

#### 응답 필드 설명
| 필드 | 타입 | 설명 |
|------|------|------|
| `content` | Array | HOT 게시글 목록 |
| `totalElements` | Long | 전체 HOT 게시글 수 |
| `currentPage` | Integer | 현재 페이지 번호 |
| `totalPages` | Integer | 전체 페이지 수 |
| `hasNext` | Boolean | 다음 페이지 존재 여부 |
| `hasPrevious` | Boolean | 이전 페이지 존재 여부 |

#### 게시글 필드 설명
| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 게시글 ID |
| `title` | String | 게시글 제목 |
| `content` | String | 게시글 내용 |
| `authorNickname` | String | 작성자 닉네임 |
| `authorUsername` | String | 작성자 이메일 |
| `authorId` | Long | 작성자 ID |
| `isAnonymous` | Boolean | 익명 게시글 여부 |
| `anonymousNickname` | String | 익명 닉네임 (익명인 경우) |
| `authorUserType` | String | 작성자 유형 (STUDENT, GRADUATE 등) |
| `createdAt` | String | 작성 시간 |
| `updatedAt` | String | 수정 시간 |
| `likeCount` | Integer | 좋아요 수 |
| `commentCount` | Integer | 댓글 수 |
| `viewCount` | Integer | 조회수 |
| `hotScore` | Integer | HOT 점수 |
| `isHot` | Boolean | HOT 게시글 여부 |
| `hotUpdatedAt` | String | HOT 점수 업데이트 시간 |
| `tagNames` | Array | 태그 목록 |

### 2. HOT 게시글 통계 조회 (관리자용)

#### 엔드포인트
```
GET /api/admin/hot-stats
```

#### 요청 예시
```bash
curl -X GET "http://localhost:8080/api/admin/hot-stats" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### 응답 예시
```json
{
  "totalHotPosts": 15,
  "todayHotPosts": 3,
  "averageHotScore": 95.6,
  "todayAverageHotScore": 102.3,
  "lastUpdated": "2025-07-19T16:00:00"
}
```

#### 응답 필드 설명
| 필드 | 타입 | 설명 |
|------|------|------|
| `totalHotPosts` | Long | 전체 HOT 게시글 수 |
| `todayHotPosts` | Long | 오늘 HOT 게시글 수 |
| `averageHotScore` | Double | 전체 평균 HOT 점수 |
| `todayAverageHotScore` | Double | 오늘 평균 HOT 점수 |
| `lastUpdated` | String | 통계 업데이트 시간 |

### 3. HOT 점수 수동 조정 (관리자용)

#### 엔드포인트
```
POST /api/admin/posts/{postId}/hot-score
```

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `postId` | Long | ✅ | 게시글 ID (Path Variable) |
| `hotScore` | Integer | ✅ | 설정할 HOT 점수 |

#### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/admin/posts/46/hot-score" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -d '{
    "hotScore": 100
  }'
```

#### 응답 예시
```json
{
  "message": "HOT 점수가 85에서 100으로 조정되었습니다. HOT 게시글로 선정되었습니다.",
  "oldHotScore": 85,
  "newHotScore": 100,
  "isHot": true,
  "success": true
}
```

#### 응답 필드 설명
| 필드 | 타입 | 설명 |
|------|------|------|
| `message` | String | 조정 결과 메시지 |
| `oldHotScore` | Integer | 이전 HOT 점수 |
| `newHotScore` | Integer | 새로운 HOT 점수 |
| `isHot` | Boolean | HOT 게시글 여부 |
| `success` | Boolean | 조정 성공 여부 |

---

## 🔧 Flutter/Dart 구현 가이드

### 1. DTO 클래스 정의

#### HotPostResponse
```dart
class HotPostResponse {
  final int id;
  final String title;
  final String content;
  final String authorNickname;
  final String authorUsername;
  final int authorId;
  final bool isAnonymous;
  final String? anonymousNickname;
  final String authorUserType;
  final DateTime createdAt;
  final DateTime? updatedAt;
  final int likeCount;
  final int commentCount;
  final int viewCount;
  final int hotScore;
  final bool isHot;
  final DateTime hotUpdatedAt;
  final List<String> tagNames;

  HotPostResponse({
    required this.id,
    required this.title,
    required this.content,
    required this.authorNickname,
    required this.authorUsername,
    required this.authorId,
    required this.isAnonymous,
    this.anonymousNickname,
    required this.authorUserType,
    required this.createdAt,
    this.updatedAt,
    required this.likeCount,
    required this.commentCount,
    required this.viewCount,
    required this.hotScore,
    required this.isHot,
    required this.hotUpdatedAt,
    required this.tagNames,
  });

  factory HotPostResponse.fromJson(Map<String, dynamic> json) {
    return HotPostResponse(
      id: json['id'],
      title: json['title'],
      content: json['content'],
      authorNickname: json['authorNickname'],
      authorUsername: json['authorUsername'],
      authorId: json['authorId'],
      isAnonymous: json['isAnonymous'],
      anonymousNickname: json['anonymousNickname'],
      authorUserType: json['authorUserType'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
      likeCount: json['likeCount'],
      commentCount: json['commentCount'],
      viewCount: json['viewCount'],
      hotScore: json['hotScore'],
      isHot: json['isHot'],
      hotUpdatedAt: DateTime.parse(json['hotUpdatedAt']),
      tagNames: List<String>.from(json['tagNames']),
    );
  }
}
```

#### HotPostListResponse
```dart
class HotPostListResponse {
  final List<HotPostResponse> content;
  final int totalElements;
  final int currentPage;
  final int totalPages;
  final bool hasNext;
  final bool hasPrevious;

  HotPostListResponse({
    required this.content,
    required this.totalElements,
    required this.currentPage,
    required this.totalPages,
    required this.hasNext,
    required this.hasPrevious,
  });

  factory HotPostListResponse.fromJson(Map<String, dynamic> json) {
    return HotPostListResponse(
      content: (json['content'] as List)
          .map((item) => HotPostResponse.fromJson(item))
          .toList(),
      totalElements: json['totalElements'],
      currentPage: json['currentPage'],
      totalPages: json['totalPages'],
      hasNext: json['hasNext'],
      hasPrevious: json['hasPrevious'],
    );
  }
}
```

#### HotStatsResponse
```dart
class HotStatsResponse {
  final int totalHotPosts;
  final int todayHotPosts;
  final double averageHotScore;
  final double todayAverageHotScore;
  final DateTime lastUpdated;

  HotStatsResponse({
    required this.totalHotPosts,
    required this.todayHotPosts,
    required this.averageHotScore,
    required this.todayAverageHotScore,
    required this.lastUpdated,
  });

  factory HotStatsResponse.fromJson(Map<String, dynamic> json) {
    return HotStatsResponse(
      totalHotPosts: json['totalHotPosts'],
      todayHotPosts: json['todayHotPosts'],
      averageHotScore: json['averageHotScore'].toDouble(),
      todayAverageHotScore: json['todayAverageHotScore'].toDouble(),
      lastUpdated: DateTime.parse(json['lastUpdated']),
    );
  }
}
```

### 2. API 서비스 클래스

```dart
class HotPostService {
  final Dio dio;
  
  HotPostService(this.dio);
  
  /// HOT 게시글 목록 조회
  Future<HotPostListResponse> getHotPosts({
    int page = 0,
    int size = 10,
    int period = 0,
  }) async {
    try {
      final response = await dio.get(
        '/api/posts/hot',
        queryParameters: {
          'page': page,
          'size': size,
          if (period > 0) 'period': period,
        },
      );
      
      return HotPostListResponse.fromJson(response.data);
    } catch (e) {
      throw Exception('HOT 게시글 조회 실패: $e');
    }
  }
  
  /// HOT 게시글 통계 조회 (관리자용)
  Future<HotStatsResponse> getHotStats() async {
    try {
      final response = await dio.get('/api/admin/hot-stats');
      return HotStatsResponse.fromJson(response.data);
    } catch (e) {
      throw Exception('HOT 게시글 통계 조회 실패: $e');
    }
  }
  
  /// HOT 점수 수동 조정 (관리자용)
  Future<Map<String, dynamic>> adjustHotScore(int postId, int hotScore) async {
    try {
      final response = await dio.post(
        '/api/admin/posts/$postId/hot-score',
        data: {'hotScore': hotScore},
      );
      
      return response.data;
    } catch (e) {
      throw Exception('HOT 점수 조정 실패: $e');
    }
  }
}
```

### 3. UI 구현 예시

#### HOT 게시글 목록 화면
```dart
class HotPostScreen extends StatefulWidget {
  @override
  _HotPostScreenState createState() => _HotPostScreenState();
}

class _HotPostScreenState extends State<HotPostScreen> {
  final HotPostService _hotPostService = HotPostService(Dio());
  
  List<HotPostResponse> hotPosts = [];
  int currentPage = 0;
  bool hasMore = true;
  bool isLoading = false;
  int selectedPeriod = 0; // 0: 전체, 7: 7일, 3: 3일
  
  @override
  void initState() {
    super.initState();
    _loadHotPosts();
  }
  
  Future<void> _loadHotPosts({bool refresh = false}) async {
    if (isLoading) return;
    
    setState(() {
      isLoading = true;
    });
    
    try {
      final response = await _hotPostService.getHotPosts(
        page: refresh ? 0 : currentPage,
        size: 10,
        period: selectedPeriod,
      );
      
      setState(() {
        if (refresh) {
          hotPosts = response.content;
          currentPage = 0;
        } else {
          hotPosts.addAll(response.content);
          currentPage++;
        }
        hasMore = response.hasNext;
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('HOT 게시글 로딩 실패: $e')),
      );
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('🔥 HOT 게시글'),
        actions: [
          PopupMenuButton<int>(
            onSelected: (period) {
              setState(() {
                selectedPeriod = period;
              });
              _loadHotPosts(refresh: true);
            },
            itemBuilder: (context) => [
              PopupMenuItem(value: 0, child: Text('전체')),
              PopupMenuItem(value: 7, child: Text('최근 7일')),
              PopupMenuItem(value: 3, child: Text('최근 3일')),
            ],
            child: Padding(
              padding: EdgeInsets.all(16.0),
              child: Icon(Icons.filter_list),
            ),
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () => _loadHotPosts(refresh: true),
        child: ListView.builder(
          itemCount: hotPosts.length + (hasMore ? 1 : 0),
          itemBuilder: (context, index) {
            if (index == hotPosts.length) {
              return _buildLoadMoreButton();
            }
            
            final post = hotPosts[index];
            return HotPostCard(post: post);
          },
        ),
      ),
    );
  }
  
  Widget _buildLoadMoreButton() {
    return Center(
      child: Padding(
        padding: EdgeInsets.all(16.0),
        child: ElevatedButton(
          onPressed: isLoading ? null : () => _loadHotPosts(),
          child: isLoading 
            ? CircularProgressIndicator()
            : Text('더보기'),
        ),
      ),
    );
  }
}
```

#### HOT 게시글 카드 위젯
```dart
class HotPostCard extends StatelessWidget {
  final HotPostResponse post;
  
  const HotPostCard({Key? key, required this.post}) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.all(8.0),
      child: InkWell(
        onTap: () {
          // 게시글 상세 페이지로 이동
          Navigator.pushNamed(context, '/post-detail', arguments: post.id);
        },
        child: Padding(
          padding: EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // 제목
              Text(
                post.title,
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              SizedBox(height: 8),
              
              // 내용
              Text(
                post.content,
                style: TextStyle(fontSize: 14),
                maxLines: 3,
                overflow: TextOverflow.ellipsis,
              ),
              SizedBox(height: 12),
              
              // 통계 정보
              Row(
                children: [
                  Icon(Icons.favorite, size: 16, color: Colors.red),
                  Text(' ${post.likeCount}'),
                  SizedBox(width: 16),
                  Icon(Icons.comment, size: 16, color: Colors.blue),
                  Text(' ${post.commentCount}'),
                  SizedBox(width: 16),
                  Icon(Icons.visibility, size: 16, color: Colors.grey),
                  Text(' ${post.viewCount}'),
                ],
              ),
              SizedBox(height: 8),
              
              // HOT 점수 및 작성자 정보
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  // HOT 점수 배지
                  Container(
                    padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: Colors.orange,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      '🔥 HOT ${post.hotScore}',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  
                  // 작성자 정보
                  Text(
                    post.isAnonymous ? '익명' : post.authorNickname,
                    style: TextStyle(
                      fontSize: 12,
                      color: Colors.grey,
                    ),
                  ),
                ],
              ),
              
              // 태그
              if (post.tagNames.isNotEmpty) ...[
                SizedBox(height: 8),
                Wrap(
                  spacing: 4,
                  children: post.tagNames.map((tag) => Chip(
                    label: Text(tag, style: TextStyle(fontSize: 10)),
                    backgroundColor: Colors.grey[200],
                  )).toList(),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
```

#### 관리자 통계 화면
```dart
class HotStatsScreen extends StatefulWidget {
  @override
  _HotStatsScreenState createState() => _HotStatsScreenState();
}

class _HotStatsScreenState extends State<HotStatsScreen> {
  final HotPostService _hotPostService = HotPostService(Dio());
  HotStatsResponse? stats;
  bool isLoading = true;
  
  @override
  void initState() {
    super.initState();
    _loadStats();
  }
  
  Future<void> _loadStats() async {
    try {
      final response = await _hotPostService.getHotStats();
      setState(() {
        stats = response;
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('통계 로딩 실패: $e')),
      );
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('HOT 게시글 통계'),
        actions: [
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: _loadStats,
          ),
        ],
      ),
      body: isLoading
          ? Center(child: CircularProgressIndicator())
          : stats == null
              ? Center(child: Text('통계를 불러올 수 없습니다.'))
              : RefreshIndicator(
                  onRefresh: _loadStats,
                  child: ListView(
                    padding: EdgeInsets.all(16.0),
                    children: [
                      _buildStatCard(
                        '전체 HOT 게시글',
                        '${stats!.totalHotPosts}개',
                        Icons.local_fire_department,
                        Colors.orange,
                      ),
                      _buildStatCard(
                        '오늘 HOT 게시글',
                        '${stats!.todayHotPosts}개',
                        Icons.today,
                        Colors.blue,
                      ),
                      _buildStatCard(
                        '평균 HOT 점수',
                        '${stats!.averageHotScore.toStringAsFixed(1)}점',
                        Icons.analytics,
                        Colors.green,
                      ),
                      _buildStatCard(
                        '오늘 평균 HOT 점수',
                        '${stats!.todayAverageHotScore.toStringAsFixed(1)}점',
                        Icons.trending_up,
                        Colors.purple,
                      ),
                      _buildStatCard(
                        '마지막 업데이트',
                        DateFormat('yyyy-MM-dd HH:mm').format(stats!.lastUpdated),
                        Icons.update,
                        Colors.grey,
                      ),
                    ],
                  ),
                ),
    );
  }
  
  Widget _buildStatCard(String title, String value, IconData icon, Color color) {
    return Card(
      margin: EdgeInsets.only(bottom: 16.0),
      child: ListTile(
        leading: Icon(icon, color: color, size: 32),
        title: Text(title, style: TextStyle(fontSize: 16)),
        subtitle: Text(value, style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
      ),
    );
  }
}
```

---

## ⚠️ 주의사항

### 1. 권한 관리
- **일반 사용자**: HOT 게시글 목록 조회만 가능
- **관리자**: 통계 조회 및 HOT 점수 수동 조정 가능

### 2. 성능 고려사항
- **페이징**: 대량 데이터 처리를 위해 페이징 사용
- **캐싱**: 자주 조회되는 HOT 게시글 목록 캐싱 고려
- **로딩 상태**: API 호출 중 로딩 인디케이터 표시

### 3. 에러 처리
- **네트워크 오류**: 적절한 에러 메시지 표시
- **권한 오류**: 관리자 기능 접근 시 권한 확인
- **데이터 오류**: 응답 데이터 검증 및 기본값 설정

### 4. UX 고려사항
- **새로고침**: Pull-to-refresh 기능 제공
- **무한 스크롤**: 더보기 버튼 또는 자동 로딩
- **필터링**: 기간별 필터링 옵션 제공
- **HOT 배지**: HOT 게시글임을 명확히 표시

---

## 📊 성공 지표

### 1. 기능적 지표
- HOT 게시글 조회 성공률
- 페이징 처리 정확도
- 필터링 기능 정확도

### 2. 성능 지표
- API 응답 시간
- 이미지 로딩 시간
- 스크롤 성능

### 3. 사용자 경험 지표
- HOT 게시글 클릭률
- 사용자 체류 시간
- 재방문율

---

**이 명세서를 참고하여 HOT 게시글 기능을 클라이언트에 구현하면 됩니다!** 🚀 