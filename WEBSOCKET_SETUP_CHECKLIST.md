# WebSocket 설정 확인 체크리스트

## ✅ **현재 구현 상태 확인**

### 1. 의존성 확인
- [x] **spring-boot-starter-websocket** 의존성 추가됨
  ```gradle
  implementation 'org.springframework.boot:spring-boot-starter-websocket'
  ```
- [x] **spring-messaging** 의존성 추가됨 (spring-boot-starter-websocket에 포함됨)

### 2. 설정 클래스 확인
- [x] **@EnableWebSocketMessageBroker** 어노테이션 사용
  ```java
  @Configuration
  @EnableWebSocketMessageBroker
  public class WebSocketConfig implements WebSocketMessageBrokerConfigurer
  ```
- [x] **WebSocketMessageBrokerConfigurer** 구현
- [x] **registerStompEndpoints** 메서드에서 **"/ws"** 엔드포인트 등록
  ```java
  registry.addEndpoint("/ws")
          .setAllowedOriginPatterns("*")
          .withSockJS();
  ```
- [x] **CORS 설정** (setAllowedOriginPatterns("*"))

### 3. 인증 설정 확인
- [x] **JWT 필터에서 WebSocket 요청 허용** (SecurityConfig에서 WebSocket 경로 제외)
- [x] **WebSocket 연결 시 Authorization 헤더 처리**
  ```java
  String token = accessor.getFirstNativeHeader("Authorization");
  if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
      // JWT 토큰 검증 로직
  }
  ```

### 4. 로깅 확인
- [x] **WebSocket 관련 DEBUG 로그 활성화**
  ```properties
  logging.level.com.bootcampbox.server=DEBUG
  logging.level.org.springframework.security=DEBUG
  ```
- [x] **연결 시도/성공/실패 로그 확인**
  ```java
  log.info("WebSocket 연결 인증 성공 - 사용자: {}", username);
  log.warn("WebSocket 연결 인증 실패 - 토큰이 유효하지 않음");
  log.error("WebSocket 연결 인증 오류: ", e);
  ```

### 5. 테스트
- [x] **curl로 WebSocket 엔드포인트 접근 테스트**
  ```bash
  curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" \
       -H "Sec-WebSocket-Version: 13" -H "Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==" \
       http://localhost:8080/ws
  ```
- [x] **브라우저에서 WebSocket 연결 테스트** (websocket-test.html 제공됨)

## 🔧 **추가 설정 사항**

### 6. 메시지 브로커 설정
- [x] **Simple Broker 활성화**
  ```java
  config.enableSimpleBroker("/topic", "/queue");
  ```
- [x] **Application Destination Prefix 설정**
  ```java
  config.setApplicationDestinationPrefixes("/app");
  ```
- [x] **User Destination Prefix 설정**
  ```java
  config.setUserDestinationPrefix("/user");
  ```

### 7. 보안 인터셉터 설정
- [x] **ChannelInterceptor 구현**
  ```java
  public class WebSocketSecurityConfig implements ChannelInterceptor
  ```
- [x] **JWT 토큰 검증 로직**
- [x] **사용자 정보 WebSocket 세션에 저장**
  ```java
  accessor.setUser(() -> username);
  ```

### 8. SockJS 지원
- [x] **SockJS 활성화**
  ```java
  .withSockJS()
  ```

## 🧪 **테스트 방법**

### 1. 서버 실행 확인
```bash
./run-local.sh
```

### 2. WebSocket 연결 테스트
```bash
# JWT 토큰 획득 (로그인 후)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# WebSocket 연결 테스트 (HTML 파일 사용)
# websocket-test.html 파일을 브라우저에서 열고 JWT 토큰 입력
```

### 3. 알림 테스트
```bash
# 다른 사용자로 댓글 작성하여 알림 테스트
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"content":"테스트 댓글입니다."}'
```

## 📋 **구현된 WebSocket 기능**

### 1. 연결 관리
- ✅ WebSocket 연결: `ws://localhost:8080/ws`
- ✅ SockJS 연결: `http://localhost:8080/ws`
- ✅ JWT 토큰 기반 인증

### 2. 메시지 구독
- ✅ 개인 알림: `/user/queue/notifications`
- ✅ 읽지 않은 개수: `/user/queue/unread-count`
- ✅ 연결 상태: `/user/queue/connection`
- ✅ 공개 채널: `/topic/echo`

### 3. 메시지 전송
- ✅ 연결 확인: `/app/connect`
- ✅ 에코 테스트: `/app/echo`

### 4. 실시간 알림
- ✅ 댓글 알림 (게시글 작성자 + 기존 댓글 작성자들)
- ✅ 좋아요 알림
- ✅ 읽지 않은 알림 개수 업데이트

## ⚠️ **주의사항**

### 1. 개발환경 설정
- CORS가 `setAllowedOriginPatterns("*")`로 설정되어 있음
- 운영환경에서는 특정 도메인으로 제한 필요

### 2. 보안 고려사항
- JWT 토큰 만료 시 연결이 끊어짐
- 클라이언트에서 재연결 로직 구현 필요

### 3. 성능 고려사항
- 많은 동시 연결 시 메모리 사용량 증가
- 연결 풀 관리 필요

## 📞 **문제 해결**

### 1. 연결 실패 시
1. JWT 토큰 유효성 확인
2. 서버 로그 확인
3. 네트워크 연결 상태 확인

### 2. 알림 수신 안됨
1. WebSocket 연결 상태 확인
2. 구독 채널 확인
3. 서버 로그에서 알림 전송 확인

### 3. 성능 이슈
1. 연결 수 모니터링
2. 메모리 사용량 확인
3. 로그 레벨 조정

---

**✅ 모든 WebSocket 설정이 완료되었습니다!** 