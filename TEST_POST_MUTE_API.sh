#!/bin/bash

# 게시글 뮤트 API 테스트 스크립트
# 사용법: ./TEST_POST_MUTE_API.sh [JWT_TOKEN] [POST_ID]

BASE_URL="http://localhost:8080/api"
TOKEN=${1:-"your-jwt-token-here"}
POST_ID=${2:-"1"}

echo "🔇 게시글 뮤트 API 테스트 시작"
echo "Base URL: $BASE_URL"
echo "Token: ${TOKEN:0:20}..."
echo "Post ID: $POST_ID"
echo ""

# 1. 게시글 뮤트 상태 확인
echo "📋 1. 게시글 뮤트 상태 확인"
curl -X GET "$BASE_URL/posts/$POST_ID/mute" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""

# 2. 게시글 뮤트
echo "🔇 2. 게시글 뮤트"
curl -X POST "$BASE_URL/posts/$POST_ID/mute" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""

# 3. 뮤트 후 상태 확인
echo "📋 3. 뮤트 후 상태 확인"
curl -X GET "$BASE_URL/posts/$POST_ID/mute" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""

# 4. 뮤트된 게시글 목록 조회
echo "📋 4. 뮤트된 게시글 목록 조회"
curl -X GET "$BASE_URL/posts/muted?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""

# 5. 게시글 언뮤트
echo "🔊 5. 게시글 언뮤트"
curl -X DELETE "$BASE_URL/posts/$POST_ID/mute" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""

# 6. 언뮤트 후 상태 확인
echo "📋 6. 언뮤트 후 상태 확인"
curl -X GET "$BASE_URL/posts/$POST_ID/mute" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo "✅ 게시글 뮤트 API 테스트 완료" 