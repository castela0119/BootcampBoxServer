# 카테고리별 게시판 검색 기능 API 명세서

## 📋 개요
카테고리별 게시판에서 검색 기능을 제공하는 API입니다. 각 카테고리 내에서 제목과 내용을 검색하고, 다양한 필터와 정렬 옵션을 지원합니다.

## 🎯 지원 카테고리
- `CAREER_COUNSEL` (진로 상담)
- `LOVE_COUNSEL` (연애 상담)  
- `INCIDENT` (사건 사고)
- `VACATION` (휴가 어때)
- `COMMUNITY_BOARD` (커뮤니티)

## 🔗 API 엔드포인트

### 기본 엔드포인트
```
GET /api/posts
```

### 전체 URL 예시
```
GET /api/posts?category=CAREER_COUNSEL&search=진로&sortBy=createdAt&sortOrder=desc&page=0&size=20
```

## 📝 요청 파라미터

### 필수 파라미터
| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| `category` | String | ✅ | 게시판 카테고리 | `CAREER_COUNSEL` |

### 선택 파라미터
| 파라미터 | 타입 | 필수 | 설명 | 예시 | 기본값 |
|---------|------|------|------|------|--------|
| `search` | String | ❌ | 검색어 (2글자 이상) | `진로` | - |
| `authorUserType` | String | ❌ | 작성자 신분 필터 | `soldier`, `civilian` | - |
| `tags` | String | ❌ | 태그 필터 (콤마 구분) | `고민,진로` | - |
| `sortBy` | String | ❌ | 정렬 기준 | `createdAt`, `likeCount`, `viewCount`, `commentCount` | `createdAt` |
| `sortOrder` | String | ❌ | 정렬 방향 | `asc`, `desc` | `desc` |
| `page` | Integer | ❌ | 페이지 번호 | `0`, `1`, `2` | `0` |
| `size` | Integer | ❌ | 페이지 크기 | `10`, `20`, `50` | `20` |

## 📤 응답 형식

### 성공 응답 (200 OK)
```json
{
  "success": true,
  "data": {
    "posts": [
      {
        "id": 1,
        "title": "진로 상담 관련 게시글",
        "content": "진로에 대한 고민이 있습니다...",
        "category": {
          "id": 1,
          "name": "진로 상담",
          "englishName": "CAREER_COUNSEL"
        },
        "authorUserType": "soldier",
        "likeCount": 5,
        "commentCount": 3,
        "viewCount": 100,
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z",
        "tags": ["고민", "진로"],
        "isAnonymous": false,
        "anonymousNickname": null
      }
    ],
    "pagination": {
      "currentPage": 0,
      "totalPages": 5,
      "totalElements": 100,
      "size": 20,
      "hasNext": true,
      "hasPrevious": false
    },
    "searchInfo": {
      "searchKeyword": "진로",
      "category": "CAREER_COUNSEL",
      "resultCount": 15
    }
  }
}
```

### 에러 응답 (400 Bad Request)
```json
{
  "success": false,
  "message": "검색어는 2글자 이상 입력해주세요.",
  "error": "IllegalArgumentException",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🔍 검색 기능 상세

### 1. 검색 범위
- **제목 검색**: 게시글 제목에서 검색어 포함 여부 확인
- **내용 검색**: 게시글 내용에서 검색어 포함 여부 확인
- **대소문자 구분 없음**: 검색어를 대소문자 구분 없이 처리
- **부분 일치**: `%검색어%` 형태로 LIKE 검색

### 2. 필터링 기능
- **카테고리 필터**: 지정된 카테고리 내에서만 검색
- **작성자 신분 필터**: `soldier` (군인), `civilian` (민간인)
- **태그 필터**: 특정 태그가 포함된 게시글만 조회

### 3. 정렬 기능
- **최신순**: `sortBy=createdAt&sortOrder=desc`
- **인기순**: `sortBy=likeCount&sortOrder=desc`
- **조회순**: `sortBy=viewCount&sortOrder=desc`
- **댓글순**: `sortBy=commentCount&sortOrder=desc`

### 4. 페이지네이션
- **페이지 번호**: 0부터 시작
- **페이지 크기**: 기본값 20, 최대 100
- **총 게시글 수**: `totalElements`로 제공

## 🧪 테스트 케이스

### 1. 기본 검색
```bash
# 진로 상담 카테고리에서 '진로' 검색
curl -X GET "http://localhost:8080/api/posts?category=CAREER_COUNSEL&search=진로"
```

### 2. 정렬 조합
```bash
# 연애 상담 카테고리에서 좋아요 순 정렬
curl -X GET "http://localhost:8080/api/posts?category=LOVE_COUNSEL&sortBy=likeCount&sortOrder=desc"
```

### 3. 복합 필터
```bash
# 진로 상담 카테고리에서 '진로' 검색 + 군인 작성자 + '고민' 태그
curl -X GET "http://localhost:8080/api/posts?category=CAREER_COUNSEL&search=진로&authorUserType=soldier&tags=고민"
```

### 4. 페이지네이션
```bash
# 커뮤니티 카테고리 2페이지 조회 (페이지 크기 10)
curl -X GET "http://localhost:8080/api/posts?category=COMMUNITY_BOARD&page=1&size=10"
```

### 5. 에러 케이스
```bash
# 검색어 1글자 (에러 발생)
curl -X GET "http://localhost:8080/api/posts?category=CAREER_COUNSEL&search=a"
```

## ⚠️ 제약사항

### 1. 검색어 제약
- **최소 길이**: 2글자 이상
- **최대 길이**: 제한 없음
- **특수문자**: 지원 (이스케이프 처리됨)

### 2. 페이지네이션 제약
- **최대 페이지 크기**: 100개
- **페이지 번호**: 0부터 시작

### 3. 정렬 제약
- **지원 정렬 기준**: `createdAt`, `likeCount`, `viewCount`, `commentCount`
- **지원 정렬 방향**: `asc`, `desc`

## 🚀 성능 최적화

### 1. 데이터베이스 인덱스
- 카테고리별 검색을 위한 복합 인덱스
- 제목/내용 검색을 위한 인덱스
- 정렬을 위한 인덱스

### 2. 쿼리 최적화
- 조건에 따른 동적 쿼리 생성
- 불필요한 JOIN 최소화
- 페이징 처리 최적화

### 3. 캐싱 전략
- 자주 검색되는 키워드 결과 캐싱
- 카테고리별 게시글 수 캐싱

## 📊 모니터링

### 1. 로깅
- 검색 요청/응답 로그
- 성능 지표 로그
- 에러 로그

### 2. 메트릭
- 검색 API 응답 시간
- 검색어별 사용 빈도
- 카테고리별 검색 빈도

## 🔧 배포 고려사항

### 1. 데이터베이스 마이그레이션
- 인덱스 추가 스크립트 실행
- 기존 데이터 검증

### 2. 환경 설정
- 검색 관련 설정값 확인
- 로깅 레벨 조정

### 3. 테스트
- 단위 테스트 실행
- 통합 테스트 실행
- 성능 테스트 실행

---

## 📝 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|-----------|
| 1.0.0 | 2024-01-15 | 초기 버전 릴리즈 |
| 1.1.0 | 2024-01-16 | 태그 필터 기능 추가 |
| 1.2.0 | 2024-01-17 | 성능 최적화 및 인덱스 추가 | 