#!/bin/bash

# 알림 설정 API 테스트 스크립트
# 사용법: ./TEST_NOTIFICATION_SETTINGS_API.sh [JWT_TOKEN]

BASE_URL="http://localhost:8080/api"
TOKEN=${1:-"your-jwt-token-here"}

echo "🔔 알림 설정 API 테스트 시작"
echo "Base URL: $BASE_URL"
echo "Token: ${TOKEN:0:20}..."
echo ""

# 1. 알림 설정 조회
echo "📋 1. 알림 설정 조회"
curl -X GET "$BASE_URL/notifications/settings" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""

# 2. 알림 설정 전체 업데이트
echo "📝 2. 알림 설정 전체 업데이트"
curl -X PUT "$BASE_URL/notifications/settings" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "settings": {
      "comment": true,
      "like": false,
      "follow": true,
      "system": true
    }
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""

# 3. 특정 알림 타입 설정 업데이트 (댓글)
echo "🔧 3. 댓글 알림 설정 업데이트"
curl -X PUT "$BASE_URL/notifications/settings/comment" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "enabled": false
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""

# 4. 특정 알림 타입 설정 업데이트 (좋아요)
echo "🔧 4. 좋아요 알림 설정 업데이트"
curl -X PUT "$BASE_URL/notifications/settings/like" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "enabled": true
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""

# 5. 알림 설정 초기화
echo "🔄 5. 알림 설정 초기화"
curl -X POST "$BASE_URL/notifications/settings/reset" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""

# 6. 최종 설정 확인
echo "📋 6. 최종 알림 설정 확인"
curl -X GET "$BASE_URL/notifications/settings" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo "✅ 알림 설정 API 테스트 완료" 