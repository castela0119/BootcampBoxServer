package com.bootcampbox.server.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PostSearchCondition {
    private String keyword; // 제목/내용 검색어
    private String category; // 카테고리 (CAREER_COUNSEL, LOVE_COUNSEL, INCIDENT, VACATION, COMMUNITY_BOARD)
    private List<String> tagList; // 태그 AND 조건
    private String userType; // 작성자 유형 (ex: USER, ADMIN)
    private String authorUserType; // 작성자 신분 (soldier, civilian)
    private String sortBy; // 정렬 기준 (createdAt, likeCount, viewCount, commentCount)
    private String sortOrder; // 정렬 방향 (asc, desc)
    
    // 검색어 유효성 검사
    public boolean isValidSearchKeyword() {
        return keyword != null && keyword.trim().length() >= 2;
    }
    
    // 검색어 정규화 (앞뒤 공백 제거)
    public String getNormalizedKeyword() {
        return keyword != null ? keyword.trim() : null;
    }
    
    // 정렬 기준 유효성 검사
    public boolean isValidSortBy() {
        if (sortBy == null) return true;
        return sortBy.equals("createdAt") || 
               sortBy.equals("likeCount") || 
               sortBy.equals("viewCount") || 
               sortBy.equals("commentCount");
    }
    
    // 정렬 방향 유효성 검사
    public boolean isValidSortOrder() {
        if (sortOrder == null) return true;
        return sortOrder.equals("asc") || sortOrder.equals("desc");
    }
    
    // 카테고리 유효성 검사
    public boolean isValidCategory() {
        if (category == null) return true;
        return category.equals("CAREER_COUNSEL") || 
               category.equals("LOVE_COUNSEL") || 
               category.equals("INCIDENT") || 
               category.equals("VACATION") || 
               category.equals("COMMUNITY_BOARD") ||
               category.equals("NOTICE");
    }
} 