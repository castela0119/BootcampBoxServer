# 게시글 & 댓글 페이징 API 명세

## 📋 개요
게시글 목록 조회와 댓글 목록 조회를 위한 페이징 API 명세입니다. QueryDSL 기반으로 구현되어 성능과 확장성이 최적화되어 있습니다.

---

## 🗂️ 게시글 페이징 API

### 1. 게시글 목록 조회 (페이징 + 필터링 + 정렬)

#### 엔드포인트
```
GET /api/posts
```

#### 요청 파라미터

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| page | Integer | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | Integer | ❌ | 10 | 페이지당 항목 수 |
| search | String | ❌ | - | 검색 키워드 (제목/내용, 2글자 이상) |
| authorUserType | String | ❌ | - | 작성자 유형 필터 |
| tags | String | ❌ | - | 태그 필터 (콤마 구분, AND 조건) |
| sortBy | String | ❌ | "createdAt" | 정렬 기준 |
| sortOrder | String | ❌ | "desc" | 정렬 방향 |

#### 정렬 옵션
- **sortBy**: `likes`, `views`, `comments`, `createdAt`
- **sortOrder**: `asc`, `desc`

#### 작성자 유형 (authorUserType)
- `soldier`: 현역
- `reserve`: 예비역  
- `civilian`: 민간인
- `marines`: 해병대
- `airforce`: 공군
- `navy`: 해군
- `trainee`: 훈련병
- `관리자`: 관리자

#### 요청 예시

```bash
# 기본 조회 (최신순, 10개씩)
GET /api/posts

# 페이징
GET /api/posts?page=0&size=20

# 검색 + 필터링
GET /api/posts?search=스프링&authorUserType=soldier&tags=java,spring

# 정렬
GET /api/posts?sortBy=likes&sortOrder=desc

# 복합 조건
GET /api/posts?page=0&size=10&search=테스트&authorUserType=soldier&tags=java&sortBy=likes&sortOrder=desc
```

#### 응답 구조

```json
{
  "content": [
    {
      "id": 1,
      "title": "게시글 제목",
      "content": "게시글 내용...",
      "user": {
        "id": 1,
        "nickname": "작성자",
        "userType": "soldier"
      },
      "createdAt": "2025-07-17T21:30:00",
      "updatedAt": "2025-07-17T21:30:00",
      "isAnonymous": false,
      "anonymousNickname": null,
      "displayNickname": "작성자",
      "canBeModified": true,
      "canBeDeleted": true,
      "authorUserType": "soldier",
      "tagNames": ["java", "spring"],
      "likeCount": 5,
      "commentCount": 3,
      "viewCount": 100,
      "liked": false,
      "bookmarked": false,
      "anonymous": false
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 25,
  "totalPages": 3,
  "last": false,
  "first": true,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 10,
  "size": 10,
  "number": 0,
  "empty": false
}
```

#### Flutter 코드 예시

```dart
class PostService {
  static const String baseUrl = 'http://your-server-url:8080';
  
  // 게시글 목록 조회 (페이징 + 필터링 + 정렬)
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
      
      // 선택적 파라미터 추가
      if (search != null && search.isNotEmpty) {
        queryParams['search'] = search;
      }
      if (authorUserType != null && authorUserType.isNotEmpty) {
        queryParams['authorUserType'] = authorUserType;
      }
      if (tags != null && tags.isNotEmpty) {
        queryParams['tags'] = tags;
      }
      if (sortBy != null && sortBy.isNotEmpty) {
        queryParams['sortBy'] = sortBy;
      }
      if (sortOrder != null && sortOrder.isNotEmpty) {
        queryParams['sortOrder'] = sortOrder;
      }
      
      final uri = Uri.parse('$baseUrl/api/posts')
          .replace(queryParameters: queryParams);
      
      final response = await http.get(
        uri,
        headers: {
          'Content-Type': 'application/json',
        },
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

// 응답 모델
class PostListResponse {
  final List<Post> content;
  final int totalElements;
  final int totalPages;
  final int currentPage;
  final bool hasNext;
  final bool hasPrevious;
  
  PostListResponse({
    required this.content,
    required this.totalElements,
    required this.totalPages,
    required this.currentPage,
    required this.hasNext,
    required this.hasPrevious,
  });
  
  factory PostListResponse.fromJson(Map<String, dynamic> json) {
    return PostListResponse(
      content: (json['content'] as List)
          .map((item) => Post.fromJson(item))
          .toList(),
      totalElements: json['totalElements'],
      totalPages: json['totalPages'],
      currentPage: json['number'],
      hasNext: json['hasNext'],
      hasPrevious: json['hasPrevious'],
    );
  }
}
```

#### UI에서 사용하는 방법

```dart
class PostListScreen extends StatefulWidget {
  @override
  _PostListScreenState createState() => _PostListScreenState();
}

class _PostListScreenState extends State<PostListScreen> {
  final ScrollController _scrollController = ScrollController();
  final List<Post> _posts = [];
  bool _isLoading = false;
  bool _hasMore = true;
  int _currentPage = 0;
  static const int _pageSize = 10;
  
  // 필터링 옵션
  String? _searchKeyword;
  String? _selectedUserType;
  String? _selectedTags;
  String _sortBy = 'createdAt';
  String _sortOrder = 'desc';
  
  @override
  void initState() {
    super.initState();
    _loadPosts(refresh: true);
    _scrollController.addListener(_onScroll);
  }
  
  Future<void> _loadPosts({bool refresh = false}) async {
    if (_isLoading) return;
    
    setState(() {
      _isLoading = true;
    });
    
    try {
      if (refresh) {
        _currentPage = 0;
        _posts.clear();
      }
      
      final response = await PostService.getPosts(
        page: _currentPage,
        size: _pageSize,
        search: _searchKeyword,
        authorUserType: _selectedUserType,
        tags: _selectedTags,
        sortBy: _sortBy,
        sortOrder: _sortOrder,
      );
      
      setState(() {
        _posts.addAll(response.content);
        _hasMore = response.hasNext;
        _currentPage++;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('게시글 로드 실패: $e')),
      );
    }
  }
  
  void _onScroll() {
    if (_scrollController.position.pixels >= 
        _scrollController.position.maxScrollExtent - 200) {
      if (_hasMore && !_isLoading) {
        _loadPosts();
      }
    }
  }
  
  Future<void> _refreshPosts() async {
    await _loadPosts(refresh: true);
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('게시글 목록'),
        actions: [
          IconButton(
            icon: Icon(Icons.filter_list),
            onPressed: _showFilterDialog,
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _refreshPosts,
        child: ListView.builder(
          controller: _scrollController,
          itemCount: _posts.length + (_hasMore ? 1 : 0),
          itemBuilder: (context, index) {
            if (index == _posts.length) {
              return _buildLoadingIndicator();
            }
            return PostCard(post: _posts[index]);
          },
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _navigateToCreatePost(),
        child: Icon(Icons.add),
      ),
    );
  }
  
  Widget _buildLoadingIndicator() {
    return _isLoading
        ? Padding(
            padding: EdgeInsets.all(16.0),
            child: Center(child: CircularProgressIndicator()),
          )
        : SizedBox.shrink();
  }
  
  void _showFilterDialog() {
    showDialog(
      context: context,
      builder: (context) => FilterDialog(
        searchKeyword: _searchKeyword,
        selectedUserType: _selectedUserType,
        selectedTags: _selectedTags,
        sortBy: _sortBy,
        sortOrder: _sortOrder,
        onApply: (search, userType, tags, sortBy, sortOrder) {
          setState(() {
            _searchKeyword = search;
            _selectedUserType = userType;
            _selectedTags = tags;
            _sortBy = sortBy;
            _sortOrder = sortOrder;
          });
          _refreshPosts();
        },
      ),
    );
  }
}
```

---

## 💬 댓글 페이징 API

### 1. 댓글 목록 조회 (페이징 + 대댓글 포함)

#### 엔드포인트
```
GET /api/posts/{postId}/comments
```

#### 요청 파라미터

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| postId | Long | ✅ | - | 게시글 ID |
| page | Integer | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | Integer | ❌ | 10 | 페이지당 항목 수 |

#### 요청 예시

```bash
# 기본 조회 (최신순, 10개씩)
GET /api/posts/1/comments

# 페이징
GET /api/posts/1/comments?page=0&size=20
```

#### 응답 구조

```json
{
  "comments": [
    {
      "id": 1,
      "content": "댓글 내용",
      "authorNickname": "댓글작성자",
      "authorUsername": "commenter",
      "authorId": 2,
      "postId": 1,
      "parentId": null,
      "createdAt": "2025-07-17T21:30:00",
      "updatedAt": "2025-07-17T21:30:00",
      "replies": [
        {
          "id": 2,
          "content": "대댓글 내용",
          "authorNickname": "대댓글작성자",
          "authorUsername": "replier",
          "authorId": 3,
          "postId": 1,
          "parentId": 1,
          "createdAt": "2025-07-17T21:35:00",
          "updatedAt": "2025-07-17T21:35:00",
          "replies": [],
          "isAuthor": false,
          "likeCount": 0,
          "isLiked": false
        }
      ],
      "isAuthor": false,
      "likeCount": 2,
      "isLiked": true
    }
  ],
  "totalComments": 15,
  "currentPage": 0,
  "totalPages": 2,
  "hasNext": true,
  "hasPrevious": false
}
```

#### Flutter 코드 예시

```dart
class CommentService {
  static const String baseUrl = 'http://your-server-url:8080';
  
  // 댓글 목록 조회 (페이징 + 대댓글 포함)
  static Future<CommentListResponse> getComments({
    required int postId,
    int page = 0,
    int size = 10,
  }) async {
    try {
      final queryParams = <String, String>{
        'page': page.toString(),
        'size': size.toString(),
      };
      
      final uri = Uri.parse('$baseUrl/api/posts/$postId/comments')
          .replace(queryParameters: queryParams);
      
      final response = await http.get(
        uri,
        headers: {
          'Content-Type': 'application/json',
        },
      );
      
      if (response.statusCode == 200) {
        return CommentListResponse.fromJson(jsonDecode(response.body));
      } else {
        throw Exception('댓글 목록 조회 실패: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('댓글 목록 조회 중 오류 발생: $e');
    }
  }
}

// 응답 모델
class CommentListResponse {
  final List<Comment> comments;
  final int totalComments;
  final int currentPage;
  final int totalPages;
  final bool hasNext;
  final bool hasPrevious;
  
  CommentListResponse({
    required this.comments,
    required this.totalComments,
    required this.currentPage,
    required this.totalPages,
    required this.hasNext,
    required this.hasPrevious,
  });
  
  factory CommentListResponse.fromJson(Map<String, dynamic> json) {
    return CommentListResponse(
      comments: (json['comments'] as List)
          .map((item) => Comment.fromJson(item))
          .toList(),
      totalComments: json['totalComments'],
      currentPage: json['currentPage'],
      totalPages: json['totalPages'],
      hasNext: json['hasNext'],
      hasPrevious: json['hasPrevious'],
    );
  }
}

class Comment {
  final int id;
  final String content;
  final String authorNickname;
  final String authorUsername;
  final int authorId;
  final int postId;
  final int? parentId;
  final DateTime createdAt;
  final DateTime updatedAt;
  final List<Comment> replies;
  final bool isAuthor;
  final int likeCount;
  final bool isLiked;
  
  Comment({
    required this.id,
    required this.content,
    required this.authorNickname,
    required this.authorUsername,
    required this.authorId,
    required this.postId,
    this.parentId,
    required this.createdAt,
    required this.updatedAt,
    required this.replies,
    required this.isAuthor,
    required this.likeCount,
    required this.isLiked,
  });
  
  factory Comment.fromJson(Map<String, dynamic> json) {
    return Comment(
      id: json['id'],
      content: json['content'],
      authorNickname: json['authorNickname'],
      authorUsername: json['authorUsername'],
      authorId: json['authorId'],
      postId: json['postId'],
      parentId: json['parentId'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
      replies: (json['replies'] as List?)
          ?.map((item) => Comment.fromJson(item))
          .toList() ?? [],
      isAuthor: json['isAuthor'],
      likeCount: json['likeCount'],
      isLiked: json['isLiked'],
    );
  }
}
```

#### UI에서 사용하는 방법

```dart
class CommentListScreen extends StatefulWidget {
  final int postId;
  
  CommentListScreen({required this.postId});
  
  @override
  _CommentListScreenState createState() => _CommentListScreenState();
}

class _CommentListScreenState extends State<CommentListScreen> {
  final ScrollController _scrollController = ScrollController();
  final List<Comment> _comments = [];
  bool _isLoading = false;
  bool _hasMore = true;
  int _currentPage = 0;
  static const int _pageSize = 10;
  
  @override
  void initState() {
    super.initState();
    _loadComments(refresh: true);
    _scrollController.addListener(_onScroll);
  }
  
  Future<void> _loadComments({bool refresh = false}) async {
    if (_isLoading) return;
    
    setState(() {
      _isLoading = true;
    });
    
    try {
      if (refresh) {
        _currentPage = 0;
        _comments.clear();
      }
      
      final response = await CommentService.getComments(
        postId: widget.postId,
        page: _currentPage,
        size: _pageSize,
      );
      
      setState(() {
        _comments.addAll(response.comments);
        _hasMore = response.hasNext;
        _currentPage++;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('댓글 로드 실패: $e')),
      );
    }
  }
  
  void _onScroll() {
    if (_scrollController.position.pixels >= 
        _scrollController.position.maxScrollExtent - 200) {
      if (_hasMore && !_isLoading) {
        _loadComments();
      }
    }
  }
  
  Future<void> _refreshComments() async {
    await _loadComments(refresh: true);
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('댓글')),
      body: RefreshIndicator(
        onRefresh: _refreshComments,
        child: ListView.builder(
          controller: _scrollController,
          itemCount: _comments.length + (_hasMore ? 1 : 0),
          itemBuilder: (context, index) {
            if (index == _comments.length) {
              return _buildLoadingIndicator();
            }
            return CommentCard(
              comment: _comments[index],
              onReply: _showReplyDialog,
              onLike: _toggleLike,
            );
          },
        ),
      ),
      bottomNavigationBar: _buildCommentInput(),
    );
  }
  
  Widget _buildLoadingIndicator() {
    return _isLoading
        ? Padding(
            padding: EdgeInsets.all(16.0),
            child: Center(child: CircularProgressIndicator()),
          )
        : SizedBox.shrink();
  }
  
  Widget _buildCommentInput() {
    return Container(
      padding: EdgeInsets.all(16.0),
      child: Row(
        children: [
          Expanded(
            child: TextField(
              decoration: InputDecoration(
                hintText: '댓글을 입력하세요...',
                border: OutlineInputBorder(),
              ),
            ),
          ),
          SizedBox(width: 8.0),
          ElevatedButton(
            onPressed: _addComment,
            child: Text('작성'),
          ),
        ],
      ),
    );
  }
  
  void _showReplyDialog(Comment comment) {
    // 대댓글 작성 다이얼로그
  }
  
  void _toggleLike(Comment comment) {
    // 댓글 좋아요 토글
  }
  
  void _addComment() {
    // 댓글 작성
  }
}
```

---

## 🔧 고급 기능

### 1. 무한 스크롤 구현

```dart
class InfiniteScrollMixin<T> {
  final List<T> items = [];
  final ScrollController scrollController = ScrollController();
  bool isLoading = false;
  bool hasMore = true;
  int currentPage = 0;
  static const int pageSize = 10;
  
  Future<List<T>> loadData(int page, int size);
  
  void initializeScrollListener() {
    scrollController.addListener(() {
      if (scrollController.position.pixels >= 
          scrollController.position.maxScrollExtent - 200) {
        if (hasMore && !isLoading) {
          loadMore();
        }
      }
    });
  }
  
  Future<void> loadMore() async {
    if (isLoading) return;
    
    setState(() {
      isLoading = true;
    });
    
    try {
      final newItems = await loadData(currentPage, pageSize);
      
      setState(() {
        items.addAll(newItems);
        hasMore = newItems.length == pageSize;
        currentPage++;
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        isLoading = false;
      });
      // 오류 처리
    }
  }
  
  Future<void> refresh() async {
    setState(() {
      items.clear();
      currentPage = 0;
      hasMore = true;
    });
    await loadMore();
  }
  
  void setState(VoidCallback fn) {
    // StatefulWidget의 setState 호출
  }
}
```

### 2. 필터링 및 정렬 UI

```dart
class FilterDialog extends StatefulWidget {
  final String? searchKeyword;
  final String? selectedUserType;
  final String? selectedTags;
  final String sortBy;
  final String sortOrder;
  final Function(String?, String?, String?, String, String) onApply;
  
  FilterDialog({
    this.searchKeyword,
    this.selectedUserType,
    this.selectedTags,
    required this.sortBy,
    required this.sortOrder,
    required this.onApply,
  });
  
  @override
  _FilterDialogState createState() => _FilterDialogState();
}

class _FilterDialogState extends State<FilterDialog> {
  late TextEditingController _searchController;
  String? _selectedUserType;
  String? _selectedTags;
  late String _sortBy;
  late String _sortOrder;
  
  @override
  void initState() {
    super.initState();
    _searchController = TextEditingController(text: widget.searchKeyword);
    _selectedUserType = widget.selectedUserType;
    _selectedTags = widget.selectedTags;
    _sortBy = widget.sortBy;
    _sortOrder = widget.sortOrder;
  }
  
  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text('필터 및 정렬'),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _searchController,
              decoration: InputDecoration(
                labelText: '검색어',
                hintText: '제목 또는 내용으로 검색',
              ),
            ),
            SizedBox(height: 16),
            DropdownButtonFormField<String>(
              value: _selectedUserType,
              decoration: InputDecoration(labelText: '작성자 유형'),
              items: [
                DropdownMenuItem(value: null, child: Text('전체')),
                DropdownMenuItem(value: 'soldier', child: Text('현역')),
                DropdownMenuItem(value: 'reserve', child: Text('예비역')),
                DropdownMenuItem(value: 'civilian', child: Text('민간인')),
                // ... 기타 유형들
              ],
              onChanged: (value) {
                setState(() {
                  _selectedUserType = value;
                });
              },
            ),
            SizedBox(height: 16),
            TextField(
              decoration: InputDecoration(
                labelText: '태그',
                hintText: '콤마로 구분 (예: java,spring)',
              ),
              onChanged: (value) {
                _selectedTags = value;
              },
            ),
            SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: DropdownButtonFormField<String>(
                    value: _sortBy,
                    decoration: InputDecoration(labelText: '정렬 기준'),
                    items: [
                      DropdownMenuItem(value: 'createdAt', child: Text('작성일')),
                      DropdownMenuItem(value: 'likes', child: Text('좋아요')),
                      DropdownMenuItem(value: 'views', child: Text('조회수')),
                      DropdownMenuItem(value: 'comments', child: Text('댓글수')),
                    ],
                    onChanged: (value) {
                      setState(() {
                        _sortBy = value!;
                      });
                    },
                  ),
                ),
                SizedBox(width: 8),
                Expanded(
                  child: DropdownButtonFormField<String>(
                    value: _sortOrder,
                    decoration: InputDecoration(labelText: '정렬 방향'),
                    items: [
                      DropdownMenuItem(value: 'desc', child: Text('내림차순')),
                      DropdownMenuItem(value: 'asc', child: Text('오름차순')),
                    ],
                    onChanged: (value) {
                      setState(() {
                        _sortOrder = value!;
                      });
                    },
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: Text('취소'),
        ),
        ElevatedButton(
          onPressed: () {
            widget.onApply(
              _searchController.text.isEmpty ? null : _searchController.text,
              _selectedUserType,
              _selectedTags,
              _sortBy,
              _sortOrder,
            );
            Navigator.pop(context);
          },
          child: Text('적용'),
        ),
      ],
    );
  }
}
```

---

## ⚠️ 주의사항

### 1. 성능 최적화
- **페이지 크기**: 한 번에 너무 많은 데이터를 로드하지 않도록 주의
- **캐싱**: 이미 로드한 데이터는 캐시하여 재사용
- **로딩 상태**: 사용자에게 로딩 상태를 명확히 표시

### 2. 오류 처리
- **네트워크 오류**: 인터넷 연결 문제 시 적절한 메시지 표시
- **서버 오류**: 500 에러 등 서버 문제 시 재시도 옵션 제공
- **빈 데이터**: 데이터가 없을 때 적절한 UI 표시

### 3. 사용자 경험
- **무한 스크롤**: 자연스러운 스크롤 경험 제공
- **새로고침**: Pull-to-refresh 기능 구현
- **필터링**: 직관적인 필터 UI 제공

---

## 📝 체크리스트

### 게시글 페이징
- [ ] 기본 페이징 구현
- [ ] 무한 스크롤 구현
- [ ] 검색 기능 구현
- [ ] 필터링 기능 구현
- [ ] 정렬 기능 구현
- [ ] 새로고침 기능 구현
- [ ] 로딩 상태 표시
- [ ] 오류 처리 구현

### 댓글 페이징
- [ ] 기본 페이징 구현
- [ ] 대댓글 포함 조회
- [ ] 무한 스크롤 구현
- [ ] 새로고침 기능 구현
- [ ] 댓글 작성 기능
- [ ] 댓글 좋아요 기능
- [ ] 대댓글 작성 기능
- [ ] 로딩 상태 표시
- [ ] 오류 처리 구현

---

## 🎯 결론

이 명세를 따라 구현하면 **성능이 최적화되고 사용자 경험이 뛰어난 페이징 기능**을 제공할 수 있습니다.

주요 특징:
1. **QueryDSL 기반**: 서버에서 최적화된 쿼리 실행
2. **유연한 필터링**: 검색, 작성자 유형, 태그 등 다양한 필터 지원
3. **동적 정렬**: 좋아요, 조회수, 댓글수 등 다양한 기준으로 정렬
4. **대댓글 지원**: 댓글에 대댓글 기능 포함
5. **무한 스크롤**: 자연스러운 스크롤 경험

이 가이드를 따라 구현하면 서버와 완벽하게 연동되는 페이징 기능을 제공할 수 있습니다! 🚀 