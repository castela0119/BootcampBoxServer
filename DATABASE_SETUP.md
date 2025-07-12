# 데이터베이스 설정 가이드

## 개요
이 프로젝트는 MySQL과 H2 데이터베이스를 모두 지원합니다. 환경에 따라 쉽게 전환할 수 있습니다.

## 환경별 실행 방법

### 1. H2 인메모리 데이터베이스 (기본값)
```bash
./run-h2.sh
```
- **장점**: 별도 설정 없이 바로 사용 가능
- **특징**: 서버 재시작 시 데이터 초기화
- **콘솔**: http://localhost:8080/h2-console (서버 실행 시)

### 2. MySQL 데이터베이스
```bash
./run-mysql.sh
```
- **사전 준비**: MySQL 서버 실행 및 데이터베이스 생성 필요
- **연결 테스트**: `./test-mysql-connection.sh`

## MySQL 설정

### 1. MySQL 서버 상태 확인
```bash
brew services list | grep mysql
```

### 2. MySQL 연결 테스트
```bash
./test-mysql-connection.sh
```

### 3. MySQL 비밀번호 설정 (필요시)
`run-mysql.sh` 파일에서 `MYSQL_PASSWORD` 변수를 수정:
```bash
MYSQL_PASSWORD="your_password_here"
```

### 4. 데이터베이스 생성
```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS rookie_db;"
```

## 환경변수 설정

### H2 환경변수
```bash
export DB_URL="jdbc:h2:mem:testdb"
export DB_USERNAME="sa"
export DB_PASSWORD=""
export DB_DRIVER="org.h2.Driver"
export DDL_AUTO="create"
export HIBERNATE_DIALECT="org.hibernate.dialect.H2Dialect"
export H2_CONSOLE_ENABLED="true"
```

### MySQL 환경변수
```bash
export DB_URL="jdbc:mysql://localhost:3306/rookie_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true"
export DB_USERNAME="root"
export DB_PASSWORD="your_password"
export DB_DRIVER="com.mysql.cj.jdbc.Driver"
export DDL_AUTO="create"
export HIBERNATE_DIALECT="org.hibernate.dialect.MySQLDialect"
export H2_CONSOLE_ENABLED="false"
```

## JPA 설정 확인

### 성공적인 JPA 초기화 로그
```
HHH000204: Processing PersistenceUnitInfo [name: default]
HHH000412: Hibernate ORM core version 6.6.18.Final
HHH000206: hibernate.properties not found
HHH000021: Bytecode provider name : byte-buddy
HHH000025: JPA EntityManagerFactory name: default
```

### 엔티티 등록 확인
```
HHH000157: LazyInitializationException: could not initialize proxy [com.bootcampbox.server.domain.User#1] - no Session
```

## 문제 해결

### 1. "Table 'USERS' not found" 오류
- **원인**: 데이터베이스에 테이블이 생성되지 않음
- **해결**: `DDL_AUTO=create` 설정 확인

### 2. MySQL 연결 실패
- **원인**: MySQL 서버가 실행되지 않음 또는 비밀번호 오류
- **해결**: 
  ```bash
  brew services start mysql
  ./test-mysql-connection.sh
  ```

### 3. JPA 엔티티 인식 실패
- **원인**: `@Entity` 어노테이션 누락 또는 패키지 스캔 문제
- **해결**: 메인 클래스에 `@EntityScan` 추가

## API 테스트

### 회원가입 테스트
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber":"010-1234-5678",
    "nickname":"testuser",
    "password":"password123!",
    "userType":"STUDENT"
  }'
```

### 성공 응답
```json
{
  "id": 1,
  "phoneNumber": "010-1234-5678",
  "nickname": "testuser",
  "userType": "STUDENT",
  "role": "USER"
}
``` 