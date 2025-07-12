#!/bin/bash

echo "=== MySQL 연결 테스트 ==="

# MySQL 비밀번호 설정 (필요시 수정)
MYSQL_PASSWORD=""

echo "MySQL 연결을 테스트합니다..."

if [ -n "$MYSQL_PASSWORD" ]; then
    mysql -u root -p"$MYSQL_PASSWORD" -e "SELECT 1;" 2>/dev/null
else
    mysql -u root -e "SELECT 1;" 2>/dev/null
fi

if [ $? -eq 0 ]; then
    echo "✅ MySQL 연결 성공!"
    
    echo "데이터베이스 목록:"
    if [ -n "$MYSQL_PASSWORD" ]; then
        mysql -u root -p"$MYSQL_PASSWORD" -e "SHOW DATABASES;"
    else
        mysql -u root -e "SHOW DATABASES;"
    fi
    
    echo ""
    echo "rookie_db 데이터베이스 생성:"
    if [ -n "$MYSQL_PASSWORD" ]; then
        mysql -u root -p"$MYSQL_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS rookie_db;"
    else
        mysql -u root -e "CREATE DATABASE IF NOT EXISTS rookie_db;"
    fi
    
    echo "✅ rookie_db 데이터베이스 준비 완료!"
else
    echo "❌ MySQL 연결 실패!"
    echo "다음 중 하나를 확인하세요:"
    echo "1. MySQL이 실행 중인지 확인: brew services list | grep mysql"
    echo "2. MySQL 비밀번호가 올바른지 확인"
    echo "3. MySQL 비밀번호 재설정: mysql_secure_installation"
fi 