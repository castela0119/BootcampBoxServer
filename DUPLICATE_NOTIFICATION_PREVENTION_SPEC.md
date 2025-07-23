# 중복 알림 방지 기능 명세서

## 📋 **기능 개요**

사용자가 같은 게시글에 좋아요를 취소하고 다시 좋아요를 누르는 등의 반복적인 액션으로 인한 중복 알림을 방지하는 기능입니다.

## 🎯 **중복 알림 방지 규칙**

### 1. 같은 사용자-게시글 조합
- **규칙**: 한 사용자가 같은 게시글에 좋아요를 여러 번 눌러도 알림은 한 번만 발송
- **기간**: 24시간 내
- **예시**: 사용자 A가 게시글 1에 좋아요 → 취소 → 다시 좋아요 → 알림 발송 안함

### 2. 좋아요 취소 후 재좋아요
- **규칙**: 좋아요를 취소하고 다시 좋아요를 눌러도 알림 발송 안함
- **기간**: 24시간 내
- **예시**: 사용자 A가 게시글 1에 좋아요 → 취소 → 1시간 후 다시 좋아요 → 알림 발송 안함

### 3. 시간 제한
- **규칙**: 일정 시간(24시간) 내에 같은 사용자-게시글 조합으로 알림 발송 안함
- **기간**: 24시간
- **예시**: 사용자 A가 게시글 1에 좋아요 → 25시간 후 다시 좋아요 → 알림 발송됨

### 4. 자신의 게시글
- **규칙**: 자신의 게시글에 좋아요를 눌러도 알림 발송 안함
- **예시**: 사용자 A가 자신이 작성한 게시글에 좋아요 → 알림 발송 안함

### 5. 알림 설정 비활성화
- **규칙**: 알림 설정이 꺼진 사용자에게 발송 안함
- **예시**: 사용자 A가 좋아요 알림을 끔 → 다른 사용자가 좋아요 → 알림 발송 안함

### 6. 뮤트된 게시글
- **규칙**: 뮤트된 게시글에 좋아요 시 알림 발송 안함
- **예시**: 사용자 A가 게시글 1을 뮤트 → 다른 사용자가 좋아요 → 알림 발송 안함

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

## 🔧 **구현된 기능**

### 1. 알림 발송 이력 테이블 생성 ✅
- `notification_history` 테이블 생성
- 복합 인덱스로 중복 방지
- 외래키 제약조건 설정

### 2. 중복 알림 방지 로직 구현 ✅
- 24시간 내 같은 사용자-게시글 조합 확인
- 좋아요/댓글 알림 모두 적용
- 알림 발송 시 이력 자동 저장

### 3. 좋아요 취소 후 재좋아요 시 알림 발송 안함 ✅
- 이력 테이블 기반으로 중복 확인
- 24시간 제한 적용

### 4. 시간 기반 중복 알림 방지 (24시간) ✅
- `LocalDateTime.now().minusHours(24)` 기준
- 동적 시간 계산

### 5. 자신의 게시글 좋아요 시 알림 발송 안함 ✅
- `recipient.getId().equals(sender.getId())` 확인
- 로그 메시지 추가

### 6. 알림 설정이 꺼진 사용자에게 발송 안함 ✅
- `settingsService.isNotificationEnabled()` 확인
- 기존 로직 유지

### 7. 뮤트된 게시글에 좋아요 시 알림 발송 안함 ✅
- `postMuteService.isPostMuted()` 확인
- 기존 로직 유지

## 📊 **알림 발송 플로우**

### 좋아요 알림 발송 플로우
```
1. 사용자가 좋아요 버튼 클릭
2. 자신의 게시글인지 확인 → 맞으면 중단
3. 게시글이 뮤트되었는지 확인 → 맞으면 중단
4. 알림 설정이 켜져 있는지 확인 → 꺼져있으면 중단
5. 24시간 내 같은 사용자-게시글 조합으로 알림 발송 여부 확인 → 있으면 중단
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

### 1. 기본 중복 방지 테스트
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

### 2. 시간 기반 테스트
```bash
# 1. 사용자 B로 좋아요 클릭
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer TOKEN_B"

# 2. 25시간 후 다시 좋아요 클릭 (알림 발송됨 - 24시간 경과)
# (실제로는 시간을 기다릴 수 없으므로 데이터베이스에서 직접 수정)
```

### 3. 자신의 게시글 테스트
```bash
# 사용자 A가 자신의 게시글에 좋아요 (알림 발송 안됨)
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer TOKEN_A"
```

### 4. 뮤트된 게시글 테스트
```bash
# 1. 사용자 A가 게시글 뮤트
curl -X POST http://localhost:8080/api/posts/1/mute \
  -H "Authorization: Bearer TOKEN_A"

# 2. 사용자 B가 뮤트된 게시글에 좋아요 (알림 발송 안됨)
curl -X POST http://localhost:8080/api/posts/1/toggle-like \
  -H "Authorization: Bearer TOKEN_B"
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
INFO  - 중복 좋아요 알림 차단 - 수신자: userA, 발신자: userB, 게시글: 1 (24시간 내 이미 발송됨)
INFO  - 자신의 게시글 좋아요 알림 차단 - 사용자: userA
INFO  - 뮤트된 게시글 좋아요 알림 차단 - 수신자: userA, 게시글: 1
INFO  - 좋아요 알림 비활성화 - 수신자: userA
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

- [x] 좋아요 알림 발송 이력 테이블 생성
- [x] 중복 알림 방지 로직 구현
- [x] 좋아요 취소 후 재좋아요 시 알림 발송 안함
- [x] 시간 기반 중복 알림 방지 (24시간)
- [x] 자신의 게시글 좋아요 시 알림 발송 안함
- [x] 알림 설정이 꺼진 사용자에게 발송 안함
- [x] 뮤트된 게시글에 좋아요 시 알림 발송 안함
- [x] 댓글 알림에도 동일한 중복 방지 로직 적용
- [x] 오래된 데이터 자동 정리 기능

---

**🎉 중복 알림 방지 기능이 완전히 구현되었습니다!**

사용자가 좋아요를 취소하고 다시 좋아요를 눌러도 24시간 내에는 중복 알림이 발송되지 않습니다. 