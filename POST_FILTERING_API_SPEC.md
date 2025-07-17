# 포스트 필터링 API 명세

## 개요
포스트 목록을 조회하는 API로, 페이징, 검색, 신분태그, 태그, 정렬 기능을 지원합니다. **모든 필터 조건은 AND로 연결**되어, 입력한 모든 조건을 동시에 만족하는 게시글만 반환합니다.

## 엔드포인트
```
GET /api/posts
```

## 요청 파라미터

### 쿼리 파라미터

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| page | Integer | ❌ | 0 | 페이지 번호 (0부터 시작) |
| size | Integer | ❌ | 10 | 페이지당 항목 수 |
| search | String | ❌ | - | 검색 키워드 (제목 또는 내용에 포함된 글자, OR 조건, 2글자 이상) |
| authorUserType | String | ❌ | - | 작성자 유형 필터 ("ROOKIE", "EXPERT", "ADMIN") |
| tags | String | ❌ | - | 태그 필터 (쉼표로 구분된 여러 태그, AND 조건) |
| sort | String | ❌ | "createdAt" | 정렬 기준 ("likes", "views", "comments", "createdAt") |
| direction | String | ❌ | "desc" | 정렬 방향 ("asc", "desc") |

## 필터링 및 정렬 동작

### 1. 복합 AND 필터링
- **모든 조건은 AND로 연결**되어, 입력한 모든 조건을 동시에 만족하는 게시글만 반환합니다.
- 예시: `search=스프링&authorUserType=EXPERT&tags=java,spring` →
  - 제목 또는 내용에 "스프링"이 포함되고,
  - 작성자 유형이 EXPERT이며,
  - java와 spring 태그가 모두 포함된 게시글만 반환

#### (1) 검색 (search)
- 클라이언트에서 입력한 검색어가 **제목 또는 내용에 포함**되면 검색 결과에 노출됩니다. (OR 조건)
- **검색어는 2글자 이상 입력해야 합니다.** (API 낭비 방지)
- 예: "시계" 검색 → 제목에 "손목시계"가 있거나, 내용에 "디지털시계"가 있으면 조회됨
- 별도의 제목/내용 구분 없이 하나의 검색 영역에서 동작
- 대소문자 구분 없음, 부분 일치 검색

#### (2) 작성자 유형 필터 (authorUserType)
- "ROOKIE": 루키 사용자
- "EXPERT": 전문가 사용자
- "ADMIN": 관리자

#### (3) 태그 필터 (tags)
- 쉼표로 구분된 여러 태그 지원
- AND 조건으로 필터링 (모든 태그가 포함된 포스트만)
- **부분검색 지원**: 태그명의 일부만 입력해도 검색 가능
- 예: "java,spring" → java 태그와 spring 태그가 모두 포함된 포스트
- 예: "jav" → "java", "javascript", "javafx" 등 "jav"가 포함된 모든 태그 검색

#### (4) 정렬 (sort)
- "likes": 좋아요 수 기준
- "views": 조회수 기준
- "comments": 댓글 수 기준
- "createdAt": 생성일 기준

## 요청 예시

### 기본 조회
```bash
GET /api/posts
```

### 페이징
```bash
GET /api/posts?page=0&size=20
```

### 제목+내용 OR 검색
```bash
GET /api/posts?search=스프링
```

### 작성자 유형 필터
```bash
GET /api/posts?authorUserType=EXPERT
```

### 태그 필터 (단일, 부분검색)
```bash
GET /api/posts?tags=java
```

### 태그 필터 (다중, AND 조건)
```bash
GET /api/posts?tags=java,spring
```

### 태그 부분검색 예시
```bash
GET /api/posts?tags=jav        # "java", "javascript", "javafx" 등 검색
GET /api/posts?tags=spr        # "spring", "springboot", "springframework" 등 검색
GET /api/posts?tags=jav,spr    # "jav"가 포함된 태그와 "spr"가 포함된 태그가 모두 있는 포스트
```

### 복합 AND 필터링
```bash
GET /api/posts?search=스프링&authorUserType=EXPERT&tags=java,spring&sort=likes&direction=desc&page=0&size=10
```

## 응답 구조

### 성공 응답 (200 OK)
```json
{
  "content": [
    {
      "id": 1,
      "title": "스프링부트 시작하기",
      "content": "스프링부트를 처음 시작하는 방법을 알아보겠습니다...",
      "author": {
        "id": 1,
        "nickname": "스프링마스터",
        "userType": "EXPERT"
      },
      "category": {
        "id": 1,
        "name": "백엔드"
      },
      "tags": [
        {
          "id": 1,
          "name": "java"
        },
        {
          "id": 2,
          "name": "spring"
        }
      ],
      "likes": 15,
      "views": 120,
      "comments": 8,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
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

### 에러 응답 (400 Bad Request)
```json
{
  "message": "검색어는 2글자 이상 입력해주세요.",
  "success": false,
  "error": "IllegalArgumentException",
  "timestamp": "2024-01-15T10:30:00"
}
```

```json
{
  "message": "잘못된 정렬 기준입니다. 사용 가능한 값: likes, views, comments, createdAt",
  "success": false,
  "error": "IllegalArgumentException",
  "timestamp": "2024-01-15T10:30:00"
}
```

## 구현 세부사항

### 아키텍처 개요
- **QueryDSL 기반 동적 쿼리**: 타입 안전성과 유지보수성을 위해 QueryDSL 사용
- **Custom Repository 패턴**: 기본 JPA 기능과 QueryDSL 기능 분리
- **검색 조건 DTO**: 파라미터를 객체로 관리하여 확장성 확보
- **BooleanBuilder**: 조건별로 동적으로 쿼리 조립

### 기술적 특징
- **타입 안전성**: QueryDSL Q클래스로 컴파일 타임에 쿼리 오류 검증
- **동적 쿼리**: 조건에 따라 쿼리가 동적으로 생성되어 성능 최적화
- **유지보수성**: Custom Repository로 복잡한 쿼리 로직 분리
- **확장성**: 새로운 필터 조건 추가 시 DTO와 Repository만 수정하면 됨

### 1. Repository 구조 (QueryDSL 기반)
```java
// Custom Repository 인터페이스
public interface PostRepositoryCustom {
    Page<Post> searchPosts(PostSearchCondition cond, Pageable pageable);
}

// Custom Repository 구현체
@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Page<Post> searchPosts(PostSearchCondition cond, Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QTag tag = QTag.tag;
        
        BooleanBuilder builder = new BooleanBuilder();
        
        // 제목/내용 검색 (2글자 이상, OR)
        if (cond.getKeyword() != null && cond.getKeyword().trim().length() >= 2) {
            builder.and(
                post.title.contains(cond.getKeyword())
                    .or(post.content.contains(cond.getKeyword()))
            );
        }
        
        // 작성자 유형
        if (cond.getUserType() != null && !cond.getUserType().isEmpty()) {
            builder.and(post.user.roleType.stringValue().eq(cond.getUserType()));
        }
        
        // 태그 AND 조건 (부분검색)
        if (cond.getTagList() != null && !cond.getTagList().isEmpty()) {
            for (String tagName : cond.getTagList()) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    builder.and(post.tags.any().name.contains(tagName.trim()));
                }
            }
        }
        
        // 정렬 및 페이징
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(cond.getSort(), post);
        
        JPAQuery<Post> query = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.tags, tag).fetchJoin()
                .where(builder)
                .distinct();
        
        if (orderSpecifier != null) {
            query.orderBy(orderSpecifier);
        }
        
        List<Post> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        long total = queryFactory.selectFrom(post)
                .where(builder)
                .fetchCount();
        
        return new PageImpl<>(content, pageable, total);
    }
}
```

### 2. 검색 조건 DTO
```java
@Getter
@Setter
public class PostSearchCondition {
    private String keyword; // 제목/내용 검색어
    private List<String> tagList; // 태그 AND 조건
    private String userType; // 작성자 유형 (ex: USER, ADMIN)
    private String sort; // 정렬 기준 (likes, views, comments, createdAt 등)
}
```

### 3. Service 로직
```java
public Page<PostDto.Response> getAllPostsWithFilters(
        int page, int size, String search, String authorUserType, 
        String tags, String sortBy, String sortOrder) {
    Pageable pageable = createPageable(page, size, sortBy, sortOrder);

    // 검색 조건 객체 생성
    PostSearchCondition cond = new PostSearchCondition();
    if (search != null && !search.trim().isEmpty()) {
        String trimmedSearch = search.trim();
        if (trimmedSearch.length() >= 2) {
            cond.setKeyword(trimmedSearch);
        } else {
            throw new IllegalArgumentException("검색어는 2글자 이상 입력해주세요.");
        }
    }
    if (authorUserType != null && !authorUserType.trim().isEmpty()) {
        cond.setUserType(authorUserType.trim());
    }
    if (tags != null && !tags.trim().isEmpty()) {
        List<String> tagList = Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
        cond.setTagList(tagList);
    }
    if (sortBy != null && !sortBy.trim().isEmpty()) {
        cond.setSort(sortBy.trim());
    }

    Page<Post> postPage = postRepository.searchPosts(cond, pageable);
    return postPage.map(PostDto.Response::from);
}
```

### 4. Controller 엔드포인트
```java
@GetMapping
public ResponseEntity<Page<PostDto.Response>> getAllPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String authorUserType,
        @RequestParam(required = false) String tags, // 복수 태그 (콤마 구분)
        @RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String sortOrder) {
    
    log.info("게시글 목록 조회 요청: page={}, size={}, search={}, authorUserType={}, tags={}, sortBy={}, sortOrder={}", 
            page, size, search, authorUserType, tags, sortBy, sortOrder);
    
    Page<PostDto.Response> response = postService.getAllPostsWithFilters(
            page, size, search, authorUserType, tags, sortBy, sortOrder);
    return ResponseEntity.ok(response);
}
```

## 사용 예시

### 1. 모든 포스트 조회 (기본)
```bash
curl -X GET "http://localhost:8080/api/posts"
```

### 2. 제목+내용 OR 검색
```bash
curl -X GET "http://localhost:8080/api/posts?search=스프링"
```

### 3. 전문가 작성자의 Java 관련 포스트 조회
```bash
curl -X GET "http://localhost:8080/api/posts?authorUserType=EXPERT&tags=java&sort=likes&direction=desc"
```

### 4. Java와 Spring 태그가 모두 포함된 포스트
```bash
curl -X GET "http://localhost:8080/api/posts?tags=java,spring&sort=createdAt&direction=desc"
```

### 5. 복합 AND 필터링
```bash
curl -X GET "http://localhost:8080/api/posts?search=스프링&authorUserType=EXPERT&tags=java,spring&sort=likes&direction=desc&page=0&size=10"
```

## 주의사항

1. **모든 필터 조건은 AND로 연결**되어, 입력한 모든 조건을 동시에 만족하는 게시글만 반환됩니다.
2. **태그 필터링**: 여러 태그는 AND 조건으로 처리됩니다 (모든 태그가 포함된 포스트만 반환)
3. **검색**: 클라이언트에서 입력한 검색어가 제목 또는 내용에 포함되면 검색 결과에 노출됩니다 (OR 조건)
4. **페이징**: page는 0부터 시작하며, size는 페이지당 항목 수
5. **정렬**: 기본값은 생성일 기준 내림차순
6. **성능**: QueryDSL 기반으로 최적화된 쿼리 실행
7. **확장성**: Custom Repository 패턴으로 새로운 필터 조건 추가가 용이

## 에러 코드

| 에러 코드 | 설명 | 해결 방법 |
|-----------|------|-----------|
| 400 | 검색어가 1글자 이하 | search 파라미터를 2글자 이상으로 입력 |
| 400 | 잘못된 정렬 기준 | sort 파라미터를 "likes", "views", "comments", "createdAt" 중 하나로 설정 |
| 400 | 잘못된 정렬 방향 | direction 파라미터를 "asc" 또는 "desc"로 설정 |
| 400 | 잘못된 작성자 유형 | authorUserType 파라미터를 "ROOKIE", "EXPERT", "ADMIN" 중 하나로 설정 | 