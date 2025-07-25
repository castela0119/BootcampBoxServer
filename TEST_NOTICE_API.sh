#!/bin/bash

# 공지사항 API 테스트 스크립트
# 실행 전 서버가 실행 중인지 확인하세요

echo "=== 공지사항 API 테스트 시작 ==="

BASE_URL="http://localhost:8080/api/notices"

# 1. 공지사항 목록 조회 (인증 없이)
echo "1. 공지사항 목록 조회 (인증 없이)"
curl -s -X GET "$BASE_URL?page=0&size=5" | jq '.'

echo -e "\n"

# 2. 공지사항 상세 조회 (인증 없이)
echo "2. 공지사항 상세 조회 (인증 없이)"
curl -s -X GET "$BASE_URL/58" | jq '.'

echo -e "\n"

# 3. 공지사항 조회수 증가 (인증 없이)
echo "3. 공지사항 조회수 증가 (인증 없이)"
curl -s -X POST "$BASE_URL/58/view" | jq '.'

echo -e "\n"

# 4. 공지사항 검색 (인증 없이)
echo "4. 공지사항 검색 (인증 없이)"
curl -s -X GET "$BASE_URL?search=서비스&page=0&size=5" | jq '.'

echo -e "\n"

# 5. 공지사항 정렬 (최신순)
echo "5. 공지사항 정렬 (최신순)"
curl -s -X GET "$BASE_URL?sortBy=createdAt&sortOrder=desc&page=0&size=5" | jq '.'

echo -e "\n"

# 6. 관리자 로그인 (JWT 토큰 획득)
echo "6. 관리자 로그인 (JWT 토큰 획득)"
LOGIN_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "superadmin@test.com",
    "password": "password123"
  }')

echo "로그인 응답:"
echo "$LOGIN_RESPONSE" | jq '.'

# JWT 토큰 추출
JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken')

if [ "$JWT_TOKEN" = "null" ] || [ "$JWT_TOKEN" = "" ]; then
    echo "JWT 토큰을 가져올 수 없습니다. 로그인에 실패했습니다."
    exit 1
fi

echo -e "\nJWT 토큰: $JWT_TOKEN"
echo -e "\n"

# 7. 공지사항 작성 (관리자 권한 필요)
echo "7. 공지사항 작성 (관리자 권한 필요)"
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "title": "새로운 공지사항",
    "content": "이것은 테스트용 공지사항입니다.\n\n- 첫 번째 항목\n- 두 번째 항목\n- 세 번째 항목",
    "isAnonymous": false,
    "authorUserType": "관리자"
  }' | jq '.'

echo -e "\n"

# 8. 공지사항 수정 (관리자 권한 필요)
echo "8. 공지사항 수정 (관리자 권한 필요)"
curl -s -X PUT "$BASE_URL/58" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "title": "수정된 서비스 이용 안내",
    "content": "안녕하세요. 서비스 이용에 대한 안내사항이 수정되었습니다.\n\n1. 게시글 작성 시 매너를 지켜주세요.\n2. 개인정보 보호에 유의해주세요.\n3. 문의사항이 있으시면 관리자에게 연락해주세요.\n4. 새로운 규칙이 추가되었습니다.\n\n감사합니다.",
    "isAnonymous": false,
    "authorUserType": "관리자"
  }' | jq '.'

echo -e "\n"

# 9. 일반 사용자 로그인 (권한 없음 테스트)
echo "9. 일반 사용자 로그인 (권한 없음 테스트)"
USER_LOGIN_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user1@test.com",
    "password": "password123"
  }')

USER_JWT_TOKEN=$(echo "$USER_LOGIN_RESPONSE" | jq -r '.data.accessToken')

if [ "$USER_JWT_TOKEN" != "null" ] && [ "$USER_JWT_TOKEN" != "" ]; then
    echo "일반 사용자 JWT 토큰: $USER_JWT_TOKEN"
    
    # 10. 일반 사용자가 공지사항 작성 시도 (권한 없음)
    echo "10. 일반 사용자가 공지사항 작성 시도 (권한 없음)"
    curl -s -X POST "$BASE_URL" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $USER_JWT_TOKEN" \
      -d '{
        "title": "일반 사용자 공지사항",
        "content": "이것은 일반 사용자가 작성하려는 공지사항입니다.",
        "isAnonymous": false,
        "authorUserType": "일반"
      }' | jq '.'
else
    echo "일반 사용자 로그인에 실패했습니다."
fi

echo -e "\n"

# 11. 공지사항 삭제 (관리자 권한 필요)
echo "11. 공지사항 삭제 (관리자 권한 필요)"
curl -s -X DELETE "$BASE_URL/60" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq '.'

echo -e "\n"

# 12. 최종 공지사항 목록 확인
echo "12. 최종 공지사항 목록 확인"
curl -s -X GET "$BASE_URL?page=0&size=10" | jq '.'

echo -e "\n"

echo "=== 공지사항 API 테스트 완료 ===" 