package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Category;
import com.bootcampbox.server.dto.CategoryDto;
import com.bootcampbox.server.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 생성
    public CategoryDto.Response createCategory(CategoryDto.CreateRequest request) {
        log.info("카테고리 생성 요청: name={}, isAnonymous={}", request.getName(), request.isAnonymous());
        
        // 카테고리명 중복 확인
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리명입니다: " + request.getName());
        }

        Category category = Category.createCategory(
                request.getName(),
                request.isAnonymous(),
                request.getDescription(),
                request.getSortOrder()
        );

        Category savedCategory = categoryRepository.save(category);
        log.info("카테고리 생성 완료: id={}, name={}", savedCategory.getId(), savedCategory.getName());
        
        return CategoryDto.Response.from(savedCategory);
    }

    // 모든 활성화된 카테고리 조회
    public CategoryDto.ListResponse getAllActiveCategories() {
        log.info("활성화된 카테고리 목록 조회");
        List<Category> categories = categoryRepository.findAllActiveOrderBySortOrder();
        return CategoryDto.ListResponse.from(categories);
    }

    // 카테고리 ID로 조회
    public CategoryDto.Response getCategory(Long categoryId) {
        log.info("카테고리 조회: categoryId={}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryId));
        return CategoryDto.Response.from(category);
    }

    // 카테고리 수정
    public CategoryDto.Response updateCategory(Long categoryId, CategoryDto.UpdateRequest request) {
        log.info("카테고리 수정 요청: categoryId={}, name={}", categoryId, request.getName());
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryId));

        // 다른 카테고리와 이름 중복 확인
        if (!category.getName().equals(request.getName()) && 
            categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리명입니다: " + request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setAnonymous(request.isAnonymous());
        category.setSortOrder(request.getSortOrder());
        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);
        log.info("카테고리 수정 완료: id={}, name={}", updatedCategory.getId(), updatedCategory.getName());
        
        return CategoryDto.Response.from(updatedCategory);
    }

    // 카테고리 비활성화 (삭제 대신)
    public void deactivateCategory(Long categoryId) {
        log.info("카테고리 비활성화 요청: categoryId={}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryId));

        category.deactivate();
        categoryRepository.save(category);
        log.info("카테고리 비활성화 완료: id={}, name={}", category.getId(), category.getName());
    }

    // 카테고리 활성화
    public void activateCategory(Long categoryId) {
        log.info("카테고리 활성화 요청: categoryId={}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryId));

        category.activate();
        categoryRepository.save(category);
        log.info("카테고리 활성화 완료: id={}, name={}", category.getId(), category.getName());
    }

    // 카테고리 ID로 Category 엔티티 조회 (내부용)
    public Category getCategoryEntity(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryId));
    }

    // 카테고리명으로 Category 엔티티 조회 (내부용)
    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryName));
    }

    // 초기 카테고리 데이터 생성 (애플리케이션 시작 시)
    public void initializeDefaultCategories() {
        if (categoryRepository.count() == 0) {
            log.info("기본 카테고리 데이터 생성 시작");
            
            createDefaultCategory("자유게시판", "자유롭게 소통하는 공간", false, 1);
            createDefaultCategory("익명게시판", "익명으로 고민을 나누는 공간", true, 2);
            createDefaultCategory("고민상담", "고민을 상담받는 공간", true, 3);
            createDefaultCategory("육군게시판", "육군 관련 이야기", false, 4);
            createDefaultCategory("해군게시판", "해군 관련 이야기", false, 5);
            createDefaultCategory("공군게시판", "공군 관련 이야기", false, 6);
            createDefaultCategory("해병대게시판", "해병대 관련 이야기", false, 7);
            
            log.info("기본 카테고리 데이터 생성 완료");
        }
    }

    private void createDefaultCategory(String name, String description, boolean isAnonymous, int sortOrder) {
        Category category = Category.createCategory(name, isAnonymous, description, sortOrder);
        categoryRepository.save(category);
        log.info("기본 카테고리 생성: name={}, isAnonymous={}", name, isAnonymous);
    }
} 