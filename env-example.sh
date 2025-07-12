#!/bin/bash

# ========================================
# Rookie PX Server - Environment Variables Template
# ========================================
# 실제 사용 시 이 파일을 복사하여 env.sh로 만들고 값을 수정하세요

# ========================================
# Database Configuration
# ========================================
export DB_URL=jdbc:mysql://localhost:3306/rookie_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
export DB_USERNAME=root
export DB_PASSWORD=rookie123
export DB_DRIVER=com.mysql.cj.jdbc.Driver

# ========================================
# JPA Configuration
# ========================================
export DDL_AUTO=create
export SHOW_SQL=true
export FORMAT_SQL=true
export HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect

# ========================================
# JWT Configuration
# ========================================
export JWT_SECRET=local-secret-key-for-development-only
export JWT_EXPIRATION=86400000

# ========================================
# Server Configuration
# ========================================
export SERVER_PORT=8080
export SPRING_PROFILES_ACTIVE=local

# ========================================
# Security Configuration
# ========================================
export LOCAL_ADMIN_SECURITY_ENABLED=false

# ========================================
# Logging Configuration
# ========================================
export LOGGING_LEVEL_ROOT=INFO
export LOGGING_LEVEL_APP=DEBUG
export LOGGING_LEVEL_SECURITY=DEBUG
export LOGGING_LEVEL_SQL=DEBUG
export LOGGING_LEVEL_SQL_BINDER=TRACE

# ========================================
# H2 Database (테스트용, 필요시 주석 해제)
# ========================================
# export DB_URL=jdbc:h2:mem:testdb
# export DB_DRIVER=org.h2.Driver
# export DB_USERNAME=sa
# export DB_PASSWORD=
# export H2_CONSOLE_ENABLED=true

# ========================================
# Production Environment (운영 환경용)
# ========================================
# export SPRING_PROFILES_ACTIVE=prod
# export JWT_SECRET=your-production-secret-key-here
# export DB_URL=jdbc:mysql://your-production-db-host:3306/rookie_db
# export DB_USERNAME=your-production-username
# export DB_PASSWORD=your-production-password

# ========================================
# 사용 방법:
# ========================================
# 1. 이 파일을 복사: cp env-example.sh env.sh
# 2. env.sh 파일에서 실제 값으로 수정
# 3. 환경변수 로드: source env.sh
# 4. 서버 실행: ./gradlew bootRun 