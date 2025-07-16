# 관리자 기능 구현 계획

## 🎯 1단계: 기본 관리자 API (우선순위 높음)

### 1.1 신고 관리 API
```java
// 전체 신고 목록 조회
GET /api/admin/reports?status=PENDING&page=0&size=20

// 신고 상세 조회
GET /api/admin/reports/{reportId}

// 신고 처리
PATCH /api/admin/reports/{reportId}/process
{
  "status": "APPROVED",
  "adminComment": "신고 내용이 적절합니다.",
  "action": "DELETE_POST" // DELETE_POST, WARN_USER, BAN_USER
}
```

### 1.2 게시글 관리 API
```java
// 게시글 삭제 (관리자용)
DELETE /api/admin/posts/{postId}
{
  "reason": "부적절한 내용",
  "adminComment": "욕설이 포함된 게시글"
}

// 게시글 숨김 처리
PATCH /api/admin/posts/{postId}/hide
{
  "reason": "검토 중",
  "duration": "7" // 7일간 숨김
}
```

### 1.3 사용자 관리 API
```java
// 사용자 제재
PATCH /api/admin/users/{userId}/ban
{
  "duration": "7", // 7일 정지
  "reason": "반복적인 규칙 위반",
  "adminComment": "욕설 게시글 반복 작성"
}

// 사용자 경고
POST /api/admin/users/{userId}/warn
{
  "reason": "상업적 광고 게시",
  "adminComment": "첫 번째 경고입니다."
}
```

## 🎯 2단계: 관리자 대시보드 API

### 2.1 통계 API
```java
// 전체 통계
GET /api/admin/dashboard/stats
{
  "totalUsers": 1250,
  "totalPosts": 3456,
  "totalReports": 23,
  "pendingReports": 5,
  "todayNewUsers": 12,
  "todayNewPosts": 45
}

// 신고 통계
GET /api/admin/dashboard/reports
{
  "byType": {
    "ABUSE_DISCRIMINATION": 15,
    "COMMERCIAL_AD": 8,
    "PORNOGRAPHY_INAPPROPRIATE": 3
  },
  "byStatus": {
    "PENDING": 5,
    "APPROVED": 12,
    "REJECTED": 6
  }
}
```

### 2.2 실시간 모니터링
```java
// 최근 신고 목록
GET /api/admin/dashboard/recent-reports

// 최근 가입자
GET /api/admin/dashboard/recent-users

// 인기 게시글 (신고 위험도 포함)
GET /api/admin/dashboard/popular-posts
```

## 🎯 3단계: 고급 관리 기능

### 3.1 자동 필터링
```java
// 금지어 관리
GET /api/admin/filters/banned-words
POST /api/admin/filters/banned-words
DELETE /api/admin/filters/banned-words/{wordId}

// 자동 삭제 규칙
GET /api/admin/filters/auto-delete-rules
POST /api/admin/filters/auto-delete-rules
```

### 3.2 알림 시스템
```java
// 관리자 알림
GET /api/admin/notifications
PATCH /api/admin/notifications/{id}/read

// 신고 알림 설정
PATCH /api/admin/settings/report-notifications
{
  "emailNotification": true,
  "slackWebhook": "https://hooks.slack.com/...",
  "threshold": 3 // 3개 이상 신고 시 알림
}
```

## 📱 관리자 페이지 UI 구성

### 1. 대시보드
```
📊 통계 요약
├── 전체 사용자 수
├── 오늘 신규 가입자
├── 대기 중인 신고
└── 최근 활동

🚨 긴급 알림
├── 신고 접수 알림
├── 시스템 오류
└── 사용자 제재 만료
```

### 2. 신고 관리
```
📋 신고 목록
├── 신고 분류별 필터
├── 처리 상태별 필터
├── 신고자/신고대상 정보
└── 일괄 처리 기능

🔍 신고 상세
├── 신고 내용
├── 게시글/댓글 원문
├── 신고자 정보
└── 처리 이력
```

### 3. 사용자 관리
```
👥 사용자 목록
├── 검색/필터 기능
├── 활동 이력
├── 제재 이력
└── 권한 관리

⚖️ 제재 관리
├── 경고 발송
├── 계정 정지
├── 영구 정지
└── 제재 해제
```

### 4. 게시글 관리
```
📝 게시글 목록
├── 카테고리별 필터
├── 신고 수별 정렬
├── 최신순/인기순
└── 일괄 삭제

🛡️ 콘텐츠 검토
├── 신고된 게시글
├── 의심스러운 키워드
├── 스팸 의심 게시글
└── 자동 필터링 결과
```

## 🔧 구현 우선순위

### 🚀 Phase 1 (1-2주)
1. 기본 신고 처리 API
2. 게시글 삭제 API
3. 사용자 제재 API
4. 간단한 관리자 대시보드

### 🚀 Phase 2 (2-3주)
1. 통계 API
2. 실시간 모니터링
3. 알림 시스템
4. 고급 검색/필터

### 🚀 Phase 3 (3-4주)
1. 자동 필터링
2. 금지어 관리
3. 제재 정책 관리
4. 관리자 권한 세분화

## 💡 운영 팁

### 1. **신고 처리 원칙**
- **빠른 응답**: 24시간 내 처리
- **공정한 판단**: 객관적 기준 적용
- **투명한 의사소통**: 처리 사유 명시

### 2. **사용자 제재 원칙**
- **단계적 제재**: 경고 → 정지 → 영구정지
- **정당한 사유**: 명확한 규칙 위반 사유
- **재기회 제공**: 적절한 시점에 제재 해제

### 3. **커뮤니티 활성화**
- **긍정적 강화**: 좋은 게시글 인정
- **건설적 피드백**: 부적절한 내용에 대한 교육
- **사용자 참여**: 커뮤니티 가이드라인 공동 제작

## 🎯 결론

현재 서버에는 기본적인 신고 API가 구현되어 있으므로, **Phase 1의 관리자 기능**부터 구현하면 됩니다. 

**우선순위:**
1. 신고 처리 API 완성
2. 게시글/사용자 관리 API
3. 간단한 관리자 대시보드
4. 통계 및 모니터링

이렇게 하면 기본적인 커뮤니티 운영이 가능해집니다! 🚀 