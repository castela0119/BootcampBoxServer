#!/bin/bash

# ========================================
# Rookie PX Server - Local Development Runner
# ========================================

# 환경변수 로드
source ./load-env.sh

echo "=== 로컬 서버 시작 ==="
echo "서버 포트: $SERVER_PORT"
echo "프로파일: $SPRING_PROFILES_ACTIVE"
echo "데이터베이스: $DB_URL"
echo "======================"

# 서버 실행
./gradlew bootRun 