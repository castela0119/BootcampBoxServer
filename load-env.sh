#!/bin/bash

# ========================================
# Rookie PX Server - Environment Variables Loader
# ========================================

# env.sh 파일이 존재하는지 확인
if [ -f "env.sh" ]; then
    echo "=== 환경변수 로드 중 ==="
    source env.sh
    echo "✅ 환경변수가 성공적으로 로드되었습니다."
    echo "현재 설정:"
    echo "  - DB_URL: $DB_URL"
    echo "  - DB_USERNAME: $DB_USERNAME"
    echo "  - SERVER_PORT: $SERVER_PORT"
    echo "  - SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
    echo "  - JWT_SECRET: ${JWT_SECRET:0:20}..."
    echo "========================"
else
    echo "⚠️  env.sh 파일이 없습니다."
    echo "다음 명령어로 환경변수 파일을 생성하세요:"
    echo "  cp env-example.sh env.sh"
    echo "  # env.sh 파일을 편집하여 실제 값으로 수정"
    echo ""
    echo "또는 기본값을 사용합니다."
    
    # 기본 환경변수 설정
    export DB_URL="jdbc:mysql://localhost:3306/rookie_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true"
    export DB_USERNAME=root
    export DB_PASSWORD=rookie123
    export DB_DRIVER=com.mysql.cj.jdbc.Driver
    export DDL_AUTO=create
    export SHOW_SQL=true
    export FORMAT_SQL=true
    export HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
    export JWT_SECRET=local-secret-key-for-development-only
    export JWT_EXPIRATION=86400000
    export SERVER_PORT=8080
    export SPRING_PROFILES_ACTIVE=local
    export LOCAL_ADMIN_SECURITY_ENABLED=false
    export LOGGING_LEVEL_ROOT=INFO
    export LOGGING_LEVEL_APP=DEBUG
    export LOGGING_LEVEL_SECURITY=DEBUG
    export LOGGING_LEVEL_SQL=DEBUG
    export LOGGING_LEVEL_SQL_BINDER=TRACE
fi 