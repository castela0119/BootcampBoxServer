#!/bin/bash

# 운영 환경 변수 설정 (실제 운영에서는 환경변수로 설정)
export MYSQL_USERNAME=${MYSQL_USERNAME:-root}
export MYSQL_PASSWORD=${MYSQL_PASSWORD:-rookie123}
export JWT_SECRET=${JWT_SECRET:-production-secret-key-change-this-in-production}
export JWT_EXPIRATION=${JWT_EXPIRATION:-86400000}
export SERVER_PORT=${SERVER_PORT:-8080}
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}

echo "=== 운영 환경 설정 ==="
echo "MYSQL_USERNAME: $MYSQL_USERNAME"
echo "MYSQL_PASSWORD: [HIDDEN]"
echo "JWT_SECRET: [HIDDEN]"
echo "SERVER_PORT: $SERVER_PORT"
echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
echo "======================"

# 서버 실행
./gradlew bootRun 