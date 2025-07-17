package com.bootcampbox.server.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PostSearchCondition {
    private String keyword; // 제목/내용 검색어
    private List<String> tagList; // 태그 AND 조건
    private String userType; // 작성자 유형 (ex: USER, ADMIN)
    private String sort; // 정렬 기준 (likes, views, comments, createdAt 등)
} 