package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 상품명

    @Column(nullable = false)
    private String category; // 카테고리 (식품, 생활용품, 전자제품 등)

    @Column(nullable = false)
    private BigDecimal price; // 가격

    @Column(nullable = false)
    private String imageUrl; // 상품 이미지 URL

    @Column(nullable = false)
    private String externalUrl; // 외부 링크 (쿠팡 등)

    private String description; // 상품 설명

    private boolean isActive = true; // 활성화 여부

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
} 