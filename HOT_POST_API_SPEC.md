# 🔥 HOT 게시글 API 명세서

## 📋 개요

HOT 게시글 시스템은 사용자 참여도(좋아요, 댓글)와 시간 가중치를 기반으로 인기 게시글을 자동 선정하는 시스템입니다.

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
    }
  ],
  "totalElements": 5,
  "currentPage": 0,
  "totalPages": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

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

---

## 🔄 자동 업데이트 시스템

### 1. 이벤트 기반 업데이트
- **좋아요 추가/취소**: 즉시 HOT 점수 재계산
- **댓글 추가/삭제**: 즉시 HOT 점수 재계산
- **게시글 생성**: 초기 HOT 점수 설정

### 2. 배치 작업
- **HOT 점수 재계산**: 6시간마다 (00:00, 06:00, 12:00, 18:00)
- **오래된 게시글 제외**: 매일 새벽 2시 (7일 이상)

---

## 🗄️ 데이터베이스 스키마

### Post 테이블 필드 추가
```sql
-- HOT 관련 필드
ALTER TABLE posts ADD COLUMN hot_score INT DEFAULT 0;
ALTER TABLE posts ADD COLUMN is_hot BOOLEAN DEFAULT FALSE;
ALTER TABLE posts ADD COLUMN hot_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- 인덱스 추가
CREATE INDEX idx_posts_is_hot_hot_score ON posts(is_hot, hot_score DESC);
CREATE INDEX idx_posts_hot_updated_at ON posts(hot_updated_at DESC);
CREATE INDEX idx_posts_is_hot_created_at ON posts(is_hot, created_at DESC);
```

---

## 🔧 클라이언트 구현 가이드

### Flutter/Dart 구현 예시

#### HOT 게시글 목록 조회
```dart
class HotPostService {
  final Dio dio;
  
  HotPostService(this.dio);
  
  Future<HotPostListResponse> getHotPosts({
    int page = 0,
    int size = 10,
    int period = 0,
  }) async {
    final response = await dio.get(
      '/api/posts/hot',
      queryParameters: {
        'page': page,
        'size': size,
        if (period > 0) 'period': period,
      },
    );
    
    return HotPostListResponse.fromJson(response.data);
  }
}

// 사용 예시
class HotPostScreen extends StatefulWidget {
  @override
  _HotPostScreenState createState() => _HotPostScreenState();
}

class _HotPostScreenState extends State<HotPostScreen> {
  List<HotPostResponse> hotPosts = [];
  int currentPage = 0;
  bool hasMore = true;
  bool isLoading = false;
  
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
      final response = await hotPostService.getHotPosts(
        page: refresh ? 0 : currentPage,
        size: 10,
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
      print('HOT 게시글 로딩 오류: $e');
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
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

class HotPostCard extends StatelessWidget {
  final HotPostResponse post;
  
  const HotPostCard({Key? key, required this.post}) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.all(8.0),
      child: ListTile(
        title: Text(post.title),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(post.content),
            SizedBox(height: 8),
            Row(
              children: [
                Icon(Icons.favorite, size: 16, color: Colors.red),
                Text(' ${post.likeCount}'),
                SizedBox(width: 16),
                Icon(Icons.comment, size: 16, color: Colors.blue),
                Text(' ${post.commentCount}'),
                SizedBox(width: 16),
                Container(
                  padding: EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: Colors.orange,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    'HOT ${post.hotScore}',
                    style: TextStyle(color: Colors.white, fontSize: 12),
                  ),
                ),
              ],
            ),
          ],
        ),
        trailing: Text(
          post.authorNickname,
          style: TextStyle(fontSize: 12, color: Colors.grey),
        ),
      ),
    );
  }
}
```

#### 관리자 통계 조회
```dart
Future<HotStatsResponse> getHotStats() async {
  final response = await dio.get('/api/admin/hot-stats');
  return HotStatsResponse.fromJson(response.data);
}

// HOT 점수 수동 조정
Future<HotScoreAdjustResponse> adjustHotScore(int postId, int hotScore) async {
  final response = await dio.post(
    '/api/admin/posts/$postId/hot-score',
    data: {'hotScore': hotScore},
  );
  
  return HotScoreAdjustResponse.fromJson(response.data);
}
```

---

## ⚠️ 주의사항

### 1. 성능 고려사항
- **배치 작업**: 서버 부하가 적은 시간에 실행
- **인덱스 활용**: 조회 성능 향상을 위한 적절한 인덱스 사용
- **캐싱**: 자주 조회되는 HOT 게시글 목록 캐싱 고려

### 2. 데이터 일관성
- **이벤트 처리**: 좋아요/댓글 변경 시 즉시 업데이트
- **배치 검증**: 정기적인 점수 재검증으로 일관성 보장
- **트랜잭션**: 데이터 무결성을 위한 트랜잭션 처리

### 3. 확장성
- **Redis 캐싱**: HOT 게시글 목록 캐싱
- **분산 처리**: 대량 데이터 처리 시 분산 환경 고려
- **모니터링**: 성능 지표 및 알림 시스템 구축

---

## 📊 성공 지표

### 1. 기능적 지표
- HOT 게시글 자동 선정 정확도
- 이벤트 처리 응답 시간
- 배치 작업 성공률

### 2. 성능 지표
- API 응답 시간 (평균, 95th percentile)
- 데이터베이스 쿼리 실행 시간
- 시스템 리소스 사용률

### 3. 사용자 경험 지표
- HOT 게시글 조회 성공률
- 사용자 참여도 증가율
- 커뮤니티 활성화 지표

---

**이 명세서를 참고하여 HOT 게시글 기능을 구현하면 됩니다!** 🚀 