package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.CategoryDto;
import com.bootcampbox.server.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성 (관리자용)
    @PostMapping
    public ResponseEntity<CategoryDto.Response> createCategory(
            @Valid @RequestBody CategoryDto.CreateRequest request) {
        log.info("카테고리 생성 요청: name={}", request.getName());
        CategoryDto.Response response = categoryService.createCategory(request);
        return ResponseEntity.ok(response);
    }

    // 모든 활성화된 카테고리 조회
    @GetMapping
    public ResponseEntity<CategoryDto.ListResponse> getAllActiveCategories() {
        log.info("활성화된 카테고리 목록 조회 요청");
        CategoryDto.ListResponse response = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(response);
    }

    // 카테고리 ID로 조회
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto.Response> getCategory(@PathVariable Long categoryId) {
        log.info("카테고리 조회 요청: categoryId={}", categoryId);
        CategoryDto.Response response = categoryService.getCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    // 카테고리 수정 (관리자용)
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto.Response> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryDto.UpdateRequest request) {
        log.info("카테고리 수정 요청: categoryId={}, name={}", categoryId, request.getName());
        CategoryDto.Response response = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(response);
    }

    // 카테고리 비활성화 (관리자용)
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deactivateCategory(@PathVariable Long categoryId) {
        log.info("카테고리 비활성화 요청: categoryId={}", categoryId);
        categoryService.deactivateCategory(categoryId);
        return ResponseEntity.ok().build();
    }

    // 카테고리 활성화 (관리자용)
    @PatchMapping("/{categoryId}/activate")
    public ResponseEntity<Void> activateCategory(@PathVariable Long categoryId) {
        log.info("카테고리 활성화 요청: categoryId={}", categoryId);
        categoryService.activateCategory(categoryId);
        return ResponseEntity.ok().build();
    }
} 