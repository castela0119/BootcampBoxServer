# 알림 중복 방지 API 명세서

## 📋 **기능 개요**

사용자의 반복적인 액션(좋아요 취소 후 재좋아요, 댓글 삭제 후 재작성 등)으로 인한 중복 알림을 방지하는 기능입니다.

## 🎯 **중복 알림 방지 규칙**

### 1. 좋아요 알림 중복 방지

#### A. 게시글 좋아요
- **규칙**: 한 사용자가 같은 게시글에 좋아요를 여러 번 눌러도 알림은 한 번만 발송
- **기간**: 24시간 내
- **예시**: 사용자 A가 게시글 1에 좋아요 → 취소 → 다시 좋아요 → 알림 발송 안함

#### B. 댓글 좋아요
- **규칙**: 한 사용자가 같은 댓글에 좋아요를 여러 번 눌러도 알림은 한 번만 발송
- **기간**: 24시간 내
- **예시**: 사용자 A가 댓글 1에 좋아요 → 취소 → 다시 좋아요 → 알림 발송 안함

### 2. 댓글 알림 중복 방지

#### A. 같은 사용자-게시글 조합
- **규칙**: 한 사용자가 같은 게시글에 댓글을 여러 번 달아도 알림은 한 번만 발송
- **기간**: 24시간 내
- **예시**: 사용자 A가 게시글 1에 댓글 → 삭제 → 다시 댓글 → 알림 발송 안함

#### B. 댓글 수정
- **규칙**: 댓글을 수정해도 알림 발송 안함 (새로운 댓글이 아니므로)
- **예시**: 사용자 A가 댓글 수정 → 알림 발송 안함

#### C. 대댓글 중복 방지
- **규칙**: 같은 사용자가 같은 댓글에 대댓글을 여러 번 달아도 알림은 한 번만 발송
- **기간**: 24시간 내
- **예시**: 사용자 A가 댓글 1에 대댓글 → 삭제 → 다시 대댓글 → 알림 발송 안함

### 3. 공통 규칙

#### A. 자신의 콘텐츠
- **규칙**: 자신의 게시글/댓글에 좋아요를 눌러도 알림 발송 안함
- **예시**: 사용자 A가 자신이 작성한 게시글에 좋아요 → 알림 발송 안함

#### B. 알림 설정 비활성화
- **규칙**: 알림 설정이 꺼진 사용자에게 발송 안함
- **예시**: 사용자 A가 좋아요 알림을 끔 → 다른 사용자가 좋아요 → 알림 발송 안함

#### C. 뮤트된 게시글
- **규칙**: 뮤트된 게시글에 좋아요/댓글 시 알림 발송 안함
- **예시**: 사용자 A가 게시글 1을 뮤트 → 다른 사용자가 좋아요/댓글 → 알림 발송 안함

## 🔗 **API 엔드포인트**

### 1. 게시글 좋아요 토글
```
POST /api/posts/{postId}/toggle-like
```

### 2. 댓글 작성
```
POST /api/posts/{postId}/comments
```

### 3. 댓글 좋아요 토글
```
POST /api/posts/comments/{commentId}/toggle-like
```

### 4. 댓글 수정
```
PATCH /api/posts/comments/{commentId}
```

### 5. 댓글 삭제
```
DELETE /api/posts/comments/{commentId}
```

## 📝 **요청/응답 예시**

### 게시글 좋아요 토글
```bash
# 좋아요 추가 (첫 번째 - 알림 발송됨)
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 응답
{
  "message": "게시글에 좋아요를 눌렀습니다.",
  "likeCount": 15,
  "isLiked": true,
  "isReported": false,
  "isBookmarked": false
}

# 좋아요 취소
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 응답
{
  "message": "게시글 좋아요가 취소되었습니다.",
  "likeCount": 14,
  "isLiked": false,
  "isReported": false,
  "isBookmarked": false
}

# 다시 좋아요 (24시간 내 - 알림 발송 안됨)
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 응답 (알림은 발송 안되지만 좋아요는 정상 처리)
{
  "message": "게시글에 좋아요를 눌렀습니다.",
  "likeCount": 15,
  "isLiked": true,
  "isReported": false,
  "isBookmarked": false
}
```

### 댓글 작성
```bash
# 댓글 작성 (첫 번째 - 알림 발송됨)
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"첫 번째 댓글입니다."}'

# 댓글 삭제
curl -X DELETE http://localhost:8080/api/posts/comments/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 다시 댓글 작성 (24시간 내 - 알림 발송 안됨)
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"다시 작성한 댓글입니다."}'
```

### 댓글 수정
```bash
# 댓글 수정 (알림 발송 안됨)
curl -X PATCH http://localhost:8080/api/posts/comments/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"수정된 댓글입니다."}'
```

### 댓글 좋아요 토글
```bash
# 댓글 좋아요 (첫 번째 - 알림 발송됨)
curl -X POST http://localhost:8080/api/posts/comments/1/toggle-like \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 댓글 좋아요 취소
curl -X POST http://localhost:8080/api/posts/comments/1/toggle-like \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 다시 댓글 좋아요 (24시간 내 - 알림 발송 안됨)
curl -X POST http://localhost:8080/api/posts/comments/1/toggle-like \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🔄 **WebSocket 알림 메시지**

### 좋아요 알림
```json
{
  "type": "notification",
  "notificationId": 123,
  "senderId": 7,
  "senderNickname": "김철수",
  "notificationType": "like",
  "title": "좋아요",
  "content": "김철수님이 회원님의 게시글에 좋아요를 눌렀습니다.",
  "targetType": "post",
  "targetId": 1,
  "read": false,
  "createdAt": "2025-07-21T10:30:00",
  "unreadCount": 5
}
```

### 댓글 알림
```json
{
  "type": "notification",
  "notificationId": 124,
  "senderId": 8,
  "senderNickname": "박영희",
  "notificationType": "comment",
  "title": "새 댓글",
  "content": "박영희님이 [테스트 게시글]에 댓글을 남겼습니다.",
  "targetType": "post",
  "targetId": 1,
  "read": false,
  "createdAt": "2025-07-21T10:35:00",
  "unreadCount": 6
}
```

## 🗄️ **데이터베이스 구조**

### notification_history 테이블
```sql
CREATE TABLE notification_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL COMMENT '알림 수신자 ID',
    sender_id BIGINT NOT NULL COMMENT '알림 발신자 ID',
    notification_type VARCHAR(20) NOT NULL COMMENT '알림 타입 (like, comment)',
    target_type VARCHAR(20) NOT NULL COMMENT '대상 타입 (post, comment)',
    target_id BIGINT NOT NULL COMMENT '대상 ID',
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '발송 시간',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 복합 인덱스 (중복 방지용)
    UNIQUE KEY uk_notification_history (recipient_id, sender_id, notification_type, target_type, target_id),
    
    -- 외래키 제약조건
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- 인덱스
    INDEX idx_recipient_sender (recipient_id, sender_id),
    INDEX idx_target (target_type, target_id),
    INDEX idx_sent_at (sent_at),
    INDEX idx_notification_type (notification_type)
);
```

## 📊 **알림 발송 플로우**

### 좋아요 알림 발송 플로우
```
1. 사용자가 좋아요 버튼 클릭
2. 자신의 콘텐츠인지 확인 → 맞으면 중단
3. 게시글이 뮤트되었는지 확인 → 맞으면 중단
4. 알림 설정이 켜져 있는지 확인 → 꺼져있으면 중단
5. 24시간 내 같은 사용자-대상 조합으로 알림 발송 여부 확인 → 있으면 중단
6. 알림 생성 및 저장
7. 알림 발송 이력 저장
8. WebSocket으로 실시간 전송
```

### 댓글 알림 발송 플로우
```
1. 사용자가 댓글 작성
2. 자신의 게시글인지 확인 → 맞으면 중단
3. 게시글이 뮤트되었는지 확인 → 맞으면 중단
4. 알림 설정이 켜져 있는지 확인 → 꺼져있으면 중단
5. 24시간 내 같은 사용자-게시글 조합으로 댓글 알림 발송 여부 확인 → 있으면 중단
6. 게시글 정보 조회
7. 알림 생성 및 저장
8. 알림 발송 이력 저장
9. WebSocket으로 실시간 전송
```

## 🧪 **테스트 시나리오**

### 1. 게시글 좋아요 중복 방지 테스트
```bash
# 1. 사용자 A로 게시글 작성
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer TOKEN_A" \
  -d '{"title":"테스트 게시글","content":"테스트 내용"}'

# 2. 사용자 B로 좋아요 클릭 (첫 번째 알림 발송됨)
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer TOKEN_B"

# 3. 사용자 B로 좋아요 취소
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer TOKEN_B"

# 4. 사용자 B로 다시 좋아요 클릭 (알림 발송 안됨 - 중복 방지)
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer TOKEN_B"
```

### 2. 댓글 중복 방지 테스트
```bash
# 1. 사용자 B로 댓글 작성 (첫 번째 알림 발송됨)
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Authorization: Bearer TOKEN_B" \
  -d '{"content":"첫 번째 댓글입니다."}'

# 2. 댓글 삭제
curl -X DELETE http://localhost:8080/api/posts/comments/1 \
  -H "Authorization: Bearer TOKEN_B"

# 3. 다시 댓글 작성 (알림 발송 안됨 - 중복 방지)
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Authorization: Bearer TOKEN_B" \
  -d '{"content":"다시 작성한 댓글입니다."}'
```

### 3. 댓글 수정 테스트
```bash
# 댓글 수정 (알림 발송 안됨)
curl -X PATCH http://localhost:8080/api/posts/comments/1 \
  -H "Authorization: Bearer TOKEN_B" \
  -d '{"content":"수정된 댓글입니다."}'
```

### 4. 댓글 좋아요 중복 방지 테스트
```bash
# 1. 사용자 C로 댓글 작성
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Authorization: Bearer TOKEN_C" \
  -d '{"content":"댓글입니다."}'

# 2. 사용자 B로 댓글 좋아요 (첫 번째 알림 발송됨)
curl -X POST http://localhost:8080/api/posts/comments/2/toggle-like \
  -H "Authorization: Bearer TOKEN_B"

# 3. 사용자 B로 댓글 좋아요 취소
curl -X POST http://localhost:8080/api/posts/comments/2/toggle-like \
  -H "Authorization: Bearer TOKEN_B"

# 4. 사용자 B로 다시 댓글 좋아요 (알림 발송 안됨 - 중복 방지)
curl -X POST http://localhost:8080/api/posts/comments/2/toggle-like \
  -H "Authorization: Bearer TOKEN_B"
```

### 5. 대댓글 중복 방지 테스트
```bash
# 1. 사용자 B로 대댓글 작성 (첫 번째 알림 발송됨)
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Authorization: Bearer TOKEN_B" \
  -d '{"content":"대댓글입니다.","parentId":2}'

# 2. 대댓글 삭제
curl -X DELETE http://localhost:8080/api/posts/comments/3 \
  -H "Authorization: Bearer TOKEN_B"

# 3. 다시 대댓글 작성 (알림 발송 안됨 - 중복 방지)
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Authorization: Bearer TOKEN_B" \
  -d '{"content":"다시 작성한 대댓글입니다.","parentId":2}'
```

## 📈 **성능 고려사항**

### 1. 데이터베이스 인덱스
- 복합 인덱스로 빠른 중복 확인
- 시간 기반 쿼리 최적화

### 2. 메모리 사용량
- 알림 이력 테이블 크기 모니터링
- 30일마다 오래된 데이터 자동 정리

### 3. 쿼리 성능
- 인덱스 활용으로 빠른 조회
- 배치 작업으로 정리 작업 수행

## 🔍 **모니터링 및 로그**

### 로그 메시지 예시
```
INFO  - 좋아요 알림 생성 완료 - 수신자: userA, 발신자: userB, 대상: post:1
INFO  - 중복 좋아요 알림 차단 - 수신자: userA, 발신자: userB, 대상: post:1 (24시간 내 이미 발송됨)
INFO  - 자신의 post 좋아요 알림 차단 - 사용자: userA
INFO  - 뮤트된 게시글 좋아요 알림 차단 - 수신자: userA, 게시글: 1
INFO  - 좋아요 알림 비활성화 - 수신자: userA
INFO  - 댓글 알림 생성 완료 - 수신자: userA, 발신자: userB, 게시글: 1
INFO  - 중복 댓글 알림 차단 - 수신자: userA, 발신자: userB, 게시글: 1 (24시간 내 이미 발송됨)
```

### 모니터링 지표
- 중복 알림 차단 횟수
- 알림 발송 성공률
- 이력 테이블 크기
- 정리 작업 성공률

## ⚠️ **주의사항**

### 1. 데이터 정리
- 30일마다 오래된 이력 자동 정리
- 게시글/사용자 삭제 시 관련 이력도 삭제

### 2. 시간대 고려
- 서버 시간 기준으로 24시간 계산
- UTC 시간 사용 권장

### 3. 확장성
- 대용량 데이터 처리 시 파티셔닝 고려
- Redis 캐싱으로 성능 향상 가능

## ✅ **구현 완료 사항**

### 좋아요 알림 중복 방지
- [x] 좋아요 알림 발송 이력 테이블 생성
- [x] 중복 알림 방지 로직 구현
- [x] 좋아요 취소 후 재좋아요 시 알림 발송 안함
- [x] 시간 기반 중복 알림 방지 (24시간)
- [x] 자신의 게시글 좋아요 시 알림 발송 안함
- [x] 알림 설정이 꺼진 사용자에게 발송 안함
- [x] 뮤트된 게시글에 좋아요 시 알림 발송 안함

### 댓글 알림 중복 방지
- [x] 같은 사용자-게시글 조합 중복 방지
- [x] 댓글 삭제 후 재작성 시 알림 발송 안함
- [x] 댓글 수정 시 알림 발송 안함
- [x] 대댓글 중복 방지
- [x] 시간 제한 (24시간) 적용
- [x] 자신의 게시글 댓글 시 알림 발송 안함

### 댓글 좋아요 알림 중복 방지
- [x] 댓글 좋아요 알림 발송 이력 관리
- [x] 댓글 좋아요 취소 후 재좋아요 시 알림 발송 안함
- [x] 시간 기반 중복 알림 방지 (24시간)
- [x] 자신의 댓글 좋아요 시 알림 발송 안함

### 공통 기능
- [x] 오래된 데이터 자동 정리 기능
- [x] WebSocket 실시간 전송
- [x] 상세한 로그 메시지
- [x] 성능 최적화 (인덱스, 쿼리)

---

**🎉 알림 중복 방지 기능이 완전히 구현되었습니다!**

좋아요와 댓글 알림 모두 24시간 내 중복 발송이 방지되며, 사용자 경험이 크게 개선되었습니다. 