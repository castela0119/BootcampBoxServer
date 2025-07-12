package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.ProductDto;
import com.bootcampbox.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    // 전체 상품 목록 조회
    @GetMapping
    public ResponseEntity<ProductDto.ProductListResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        log.info("전체 상품 목록 조회 요청 - 페이지: {}, 크기: {}", page, size);
        ProductDto.ProductListResponse response = productService.getAllProducts(page, size);
        return ResponseEntity.ok(response);
    }

    // 카테고리별 상품 목록 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<ProductDto.ProductListResponse> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        log.info("카테고리별 상품 목록 조회 요청 - 카테고리: {}, 페이지: {}, 크기: {}", category, page, size);
        ProductDto.ProductListResponse response = productService.getProductsByCategory(category, page, size);
        return ResponseEntity.ok(response);
    }

    // 상품명 검색
    @GetMapping("/search")
    public ResponseEntity<ProductDto.ProductListResponse> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        log.info("상품 검색 요청 - 키워드: {}, 페이지: {}, 크기: {}", keyword, page, size);
        ProductDto.ProductListResponse response = productService.searchProducts(keyword, page, size);
        return ResponseEntity.ok(response);
    }

    // 카테고리별 상품명 검색
    @GetMapping("/category/{category}/search")
    public ResponseEntity<ProductDto.ProductListResponse> searchProductsByCategory(
            @PathVariable String category,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        log.info("카테고리별 상품 검색 요청 - 카테고리: {}, 키워드: {}, 페이지: {}, 크기: {}", category, keyword, page, size);
        ProductDto.ProductListResponse response = productService.searchProductsByCategory(category, keyword, page, size);
        return ResponseEntity.ok(response);
    }

    // 모든 카테고리 조회
    @GetMapping("/categories")
    public ResponseEntity<ProductDto.CategoryListResponse> getAllCategories() {
        log.info("모든 카테고리 조회 요청");
        ProductDto.CategoryListResponse response = productService.getAllCategories();
        return ResponseEntity.ok(response);
    }
} 