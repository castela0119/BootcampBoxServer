package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // 활성화된 카테고리만 조회 (정렬 순서대로)
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.sortOrder ASC, c.name ASC")
    List<Category> findAllActiveOrderBySortOrder();
    
    // 카테고리명으로 조회
    Optional<Category> findByName(String name);
    
    // 활성화된 카테고리명으로 조회
    Optional<Category> findByNameAndIsActiveTrue(String name);
    
    // 익명 카테고리만 조회
    @Query("SELECT c FROM Category c WHERE c.isAnonymous = true AND c.isActive = true")
    List<Category> findAllAnonymousCategories();
    
    // 카테고리명 중복 확인
    boolean existsByName(String name);
    
    // 활성화된 카테고리명 중복 확인
    boolean existsByNameAndIsActiveTrue(String name);
} 