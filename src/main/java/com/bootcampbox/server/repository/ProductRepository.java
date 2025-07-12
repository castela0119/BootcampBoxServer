package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 활성화된 상품만 조회
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    // 카테고리별 활성화된 상품 조회
    Page<Product> findByCategoryAndIsActiveTrue(String category, Pageable pageable);
    
    // 상품명 검색 (활성화된 상품만)
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.name LIKE %:keyword%")
    Page<Product> findByNameContainingAndIsActiveTrue(@Param("keyword") String keyword, Pageable pageable);
    
    // 카테고리별 상품명 검색 (활성화된 상품만)
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category = :category AND p.name LIKE %:keyword%")
    Page<Product> findByCategoryAndNameContainingAndIsActiveTrue(
            @Param("category") String category, 
            @Param("keyword") String keyword, 
            Pageable pageable);
    
    // 모든 카테고리 조회
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true")
    List<String> findAllCategories();
} 