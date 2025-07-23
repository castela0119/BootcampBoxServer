package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 카테고리명 (예: 자유게시판, 익명게시판)

    @Column(name = "english_name")
    private String englishName; // 영문 카테고리명 (예: community, career)

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous = false; // 익명 여부

    private String description; // 카테고리 설명

    @Column(name = "sort_order")
    private int sortOrder = 0; // 정렬 순서

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // 활성화 여부 (삭제 대신 비활성화)

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 카테고리 생성 메서드
    public static Category createCategory(String name, boolean isAnonymous, String description, int sortOrder) {
        Category category = new Category();
        category.setName(name);
        category.setAnonymous(isAnonymous);
        category.setDescription(description);
        category.setSortOrder(sortOrder);
        return category;
    }

    // 카테고리 비활성화 메서드
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    // 카테고리 활성화 메서드
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
} 