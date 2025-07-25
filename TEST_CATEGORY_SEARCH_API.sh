#!/bin/bash

# 카테고리별 검색 API 테스트 스크립트
# 서버가 실행 중이어야 합니다 (http://localhost:8080)

echo "=== 카테고리별 검색 API 테스트 시작 ==="

BASE_URL="http://localhost:8080/api/posts"

# 1. 기본 카테고리별 조회 테스트
echo ""
echo "1. 진로 상담 카테고리 기본 조회"
curl -s -X GET "$BASE_URL?category=CAREER_COUNSEL&page=0&size=5" | jq '.'

# 2. 검색어 포함 테스트
echo ""
echo "2. 진로 상담 카테고리에서 '진로' 검색"
curl -s -X GET "$BASE_URL?category=CAREER_COUNSEL&search=진로&page=0&size=5" | jq '.'

# 3. 정렬 테스트 (좋아요 순)
echo ""
echo "3. 연애 상담 카테고리에서 좋아요 순 정렬"
curl -s -X GET "$BASE_URL?category=LOVE_COUNSEL&sortBy=likeCount&sortOrder=desc&page=0&size=5" | jq '.'

# 4. 검색어 + 정렬 조합 테스트
echo ""
echo "4. 사건 사고 카테고리에서 '사고' 검색 후 최신순 정렬"
curl -s -X GET "$BASE_URL?category=INCIDENT&search=사고&sortBy=createdAt&sortOrder=desc&page=0&size=5" | jq '.'

# 5. 작성자 신분 필터 테스트
echo ""
echo "5. 휴가 어때 카테고리에서 군인 작성자만 조회"
curl -s -X GET "$BASE_URL?category=VACATION&authorUserType=soldier&page=0&size=5" | jq '.'

# 6. 태그 필터 테스트
echo ""
echo "6. 커뮤니티 카테고리에서 '고민' 태그 포함 게시글 조회"
curl -s -X GET "$BASE_URL?category=COMMUNITY_BOARD&tags=고민&page=0&size=5" | jq '.'

# 7. 복합 필터 테스트
echo ""
echo "7. 진로 상담 카테고리에서 '진로' 검색 + 군인 작성자 + '고민' 태그"
curl -s -X GET "$BASE_URL?category=CAREER_COUNSEL&search=진로&authorUserType=soldier&tags=고민&page=0&size=5" | jq '.'

# 8. 에러 케이스 테스트 - 검색어 1글자
echo ""
echo "8. 에러 테스트 - 검색어 1글자 (2글자 미만)"
curl -s -X GET "$BASE_URL?category=CAREER_COUNSEL&search=a&page=0&size=5" | jq '.'

# 9. 에러 케이스 테스트 - 카테고리 없음
echo ""
echo "9. 에러 테스트 - 카테고리 없음"
curl -s -X GET "$BASE_URL?search=테스트&page=0&size=5" | jq '.'

# 10. 페이지네이션 테스트
echo ""
echo "10. 페이지네이션 테스트 - 2페이지 조회"
curl -s -X GET "$BASE_URL?category=COMMUNITY_BOARD&page=1&size=3" | jq '.'

echo ""
echo "=== 카테고리별 검색 API 테스트 완료 ==="

# 응답 시간 측정 테스트
echo ""
echo "=== 성능 테스트 ==="
echo "진로 상담 카테고리 검색 응답 시간:"
time curl -s -X GET "$BASE_URL?category=CAREER_COUNSEL&search=진로&page=0&size=10" > /dev/null 