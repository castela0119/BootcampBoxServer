#!/bin/bash

echo "=== H2 인메모리 데이터베이스로 서버 실행 ==="
echo ""

# H2 환경변수 설정
export DB_URL="jdbc:h2:mem:testdb"
export DB_USERNAME="sa"
export DB_PASSWORD=""
export DB_DRIVER="org.h2.Driver"
export DDL_AUTO="create"
export SHOW_SQL="true"
export FORMAT_SQL="true"
export HIBERNATE_DIALECT="org.hibernate.dialect.H2Dialect"
export H2_CONSOLE_ENABLED="true"

echo "환경변수 설정 완료:"
echo "DB_URL: $DB_URL"
echo "DB_USERNAME: $DB_USERNAME"
echo "DB_DRIVER: $DB_DRIVER"
echo "DDL_AUTO: $DDL_AUTO"
echo "H2 Console: http://localhost:8080/h2-console"
echo ""

echo "서버를 시작합니다..."
./gradlew bootRun 