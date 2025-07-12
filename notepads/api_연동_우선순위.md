# API 연동 우선순위 체크리스트

## 1순위: 회원/계정 관련
- [ ] 회원가입(회원정보 입력)
- [ ] 내 정보 조회 (`GET /api/user/me`)
- [ ] 내 정보 수정 (`PATCH /api/user/me`)
- [ ] 비밀번호 변경/재설정 (`POST /api/auth/reset-password`)

## 2순위: 인증/보안 관련
- [ ] 로그아웃 (`POST /api/auth/logout`)
- [ ] 이메일/닉네임 중복확인 (`POST /api/auth/check-duplicate`)
- [ ] 이메일 인증코드 발송 (`POST /api/auth/send-email-code`)
- [ ] 이메일 인증코드 검증 (`POST /api/auth/verify-email-code`)

## 3순위: 커뮤니티/게시글/댓글
- [ ] 게시글 목록 조회 (`GET /api/posts`)
- [ ] 게시글 상세 조회 (`GET /api/posts/{postId}`)
- [ ] 게시글 작성 (`POST /api/posts`)
- [ ] 게시글 수정 (`PUT /api/posts/{postId}`)
- [ ] 게시글 삭제 (`DELETE /api/posts/{postId}`)
- [ ] 댓글 작성 (`POST /api/posts/{postId}/comments`)
- [ ] 댓글 조회 (`GET /api/posts/{postId}/comments`)
- [ ] 댓글 삭제 (`DELETE /api/posts/comments/{commentId}`)

## 4순위: PX 상품/알림 등 부가 기능
- [ ] PX 상품 목록/상세/검색 (`GET /api/products`, `GET /api/products/{id}`)
- [ ] 알림 목록 조회 (`GET /notifications`)
- [ ] 알림 읽음 처리 (`PATCH /notifications/read`) 

---

## 1. 서버 로그에서 에러/쿼리 확인

1. **서버 로그 실시간 확인**
   ```bash
   tail -f last_server.log
   ```
   - 마이페이지 API 호출 시점에 에러(log.error), WARN, 혹은 SQL 쿼리 로그가 찍히는지 확인
   - 예시:  
     ```
     내 정보 조회 요청: domo0119@gmail.com
     내 정보 조회 오류: ...
     ```
   - 혹은 `SELECT * FROM users WHERE email=...` 쿼리가 정상적으로 나가는지

2. **에러 메시지/스택트레이스가 있다면 복사해서 공유**  
   - 예를 들어 `IncorrectResultSizeDataAccessException`, `NullPointerException`, `NoSuchElementException` 등

---

## 2. DB에 사용자 데이터가 실제로 있는지 확인

1. **MySQL에서 직접 확인**
   ```sql
   SELECT * FROM users WHERE email = 'domo0119@gmail.com';
   ```
   - 데이터가 있으면, 닉네임/가입일 등 컬럼 값이 정상인지 확인
   - 데이터가 없으면, 회원가입을 다시 시도

---

## 3. UserService/Repository 코드 점검

- 최근에 UserService, UserRepository, MyPageController 등에서  
  **username → email**로 변경하는 작업을 했으니,  
  혹시 일부 코드가 username으로 남아있거나,  
  잘못된 파라미터로 조회하고 있지 않은지 확인

---

## 4. 환경변수/DB 연결 꼬임 확인

- 서버가 **정상적으로 올바른 DB**에 연결되어 있는지  
  (예: prod DB, local DB, H2 DB 등 혼동 여부)
- 서버 실행 시 출력되는 DB URL, 프로파일, 환경변수 값이  
  기대한 값과 일치하는지 확인

---

## 5. 캐시/빌드 문제

- 혹시 서버를 재빌드하지 않고 바로 실행했다면,  
  캐시 문제일 수 있으니 아래 명령어로 클린 빌드 후 재실행
  ```bash
  ./gradlew clean
  ./run-local.sh
  ```

---

## 6. (선택) Flutter 쪽 캐시/빌드 문제

- Flutter 앱도 hot reload/hot restart가 아니라 완전 재시작 해보기

---

## 요약 체크리스트

1. **서버 로그**에 에러/쿼리 찍히는지 확인  
2. **DB에 사용자 데이터**가 실제로 있는지 확인  
3. **UserService/Repository**에서 email로 조회하는지 코드 점검  
4. **서버 환경변수/DB 연결** 꼬임 없는지 확인  
5. **서버 클린 빌드** 후 재실행  
6. (필요시) **Flutter 완전 재시작**

---

### 추가로,  
- **서버 로그**에서 "내 정보 조회 요청: ..." 이후에 바로 에러가 찍히는지, 아니면 아무 반응이 없는지  
- **DB 쿼리 로그**가 찍히는지  
- **DB에 데이터가 실제로 있는지**  
이 세 가지를 꼭 확인해서 알려주시면,  
정확한 원인 진단이 가능합니다!

필요하다면 서버 로그/DB 쿼리 결과 캡처해서 올려주세요.  
빠르게 원인 찾아드릴 수 있습니다! 