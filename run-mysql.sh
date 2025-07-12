#!/bin/bash

echo "=== MySQL 환경으로 서버 실행 ==="
echo "MySQL 데이터베이스가 실행 중인지 확인하세요."
echo "데이터베이스 'rookie_db'가 존재하는지 확인하세요."
echo ""

# MySQL 비밀번호 설정 (필요시 수정)
MYSQL_PASSWORD=""
if [ -n "$MYSQL_PASSWORD" ]; then
    echo "MySQL 비밀번호가 설정되어 있습니다."
else
    echo "MySQL 비밀번호가 설정되지 않았습니다. 필요시 스크립트에서 MYSQL_PASSWORD 변수를 수정하세요."
fi

# MySQL 환경변수 설정
export DB_URL="jdbc:mysql://localhost:3306/rookie_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true"
export DB_USERNAME="root"
export DB_PASSWORD="$MYSQL_PASSWORD"
export DB_DRIVER="com.mysql.cj.jdbc.Driver"
export DDL_AUTO="create"
export SHOW_SQL="true"
export FORMAT_SQL="true"
export HIBERNATE_DIALECT="org.hibernate.dialect.MySQLDialect"
export H2_CONSOLE_ENABLED="false"

echo "환경변수 설정 완료:"
echo "DB_URL: $DB_URL"
echo "DB_USERNAME: $DB_USERNAME"
echo "DB_DRIVER: $DB_DRIVER"
echo "DDL_AUTO: $DDL_AUTO"
echo ""

echo "서버를 시작합니다..."
./gradlew bootRun 