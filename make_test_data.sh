#!/bin/bash

# jq 설치 안내
if ! command -v jq &> /dev/null; then
  echo "jq가 설치되어 있지 않습니다. Homebrew 사용 시: brew install jq"
  exit 1
fi

RAND=$((RANDOM % 100000))
EMAIL="test${RAND}@example.com"
PASSWORD="Password123!"
NICKNAME="테스트유저${RAND}"
USERTYPE="현역"

echo "[테스트 계정 정보]"
echo "EMAIL: $EMAIL"
echo "NICKNAME: $NICKNAME"
echo "PASSWORD: $PASSWORD"
echo "USERTYPE: $USERTYPE"

# 0. 회원가입 시도
SIGNUP_RESULT=$(curl -s -X POST http://localhost:8080/api/user/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"'$EMAIL'","password":"'$PASSWORD'","nickname":"'$NICKNAME'","userType":"'$USERTYPE'"}')

if echo "$SIGNUP_RESULT" | grep -q '회원가입이 완료되었습니다'; then
  echo "회원가입 성공: $EMAIL"
else
  echo "회원가입 시도 결과: $SIGNUP_RESULT"
fi

# 0-1. 강제 인증 API 호출
VERIFY_RESULT=$(curl -s -X POST "http://localhost:8080/api/dev/verify-email?email=$EMAIL")
echo "강제 인증 결과: $VERIFY_RESULT"

# 1. 로그인 및 토큰 발급
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"'$EMAIL'","password":"'$PASSWORD'"}' | jq -r '.data.token')

if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
  echo "로그인 실패! 이메일 인증 또는 계정 생성 필요."
  exit 1
fi

echo "JWT 토큰 발급 성공: $TOKEN"

# 2. 게시글 30개 생성
for i in {1..30}
do
  POST_ID=$(curl -s -X POST http://localhost:8080/api/posts \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"title":"테스트 게시글 '$i'","content":"본문 '$i' 입니다.","tagNames":["test","page'$i'"]}' | jq -r '.data.id')

  echo "게시글 $i 생성 (ID: $POST_ID)"

  # 3. 각 게시글에 댓글 10개 생성
  for j in {1..10}
  do
    curl -s -X POST http://localhost:8080/api/posts/$POST_ID/comments \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{"content":"댓글 '$j' for post '$i'"}' > /dev/null
  done
  echo "  → 댓글 10개 생성 완료"
done

echo "게시글 30개, 각 게시글당 댓글 10개 생성 완료!" 