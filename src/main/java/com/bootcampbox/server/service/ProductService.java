package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Product;
import com.bootcampbox.server.dto.ProductDto;
import com.bootcampbox.server.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    // 전체 상품 목록 조회 (페이징)
    public ProductDto.ProductListResponse getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByIsActiveTrue(pageable);
        
        return ProductDto.ProductListResponse.from(
                productPage.getContent(),
                productPage.getTotalElements(),
                productPage.getNumber(),
                productPage.getTotalPages(),
                productPage.hasNext(),
                productPage.hasPrevious()
        );
    }

    // 카테고리별 상품 목록 조회
    public ProductDto.ProductListResponse getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByCategoryAndIsActiveTrue(category, pageable);
        
        return ProductDto.ProductListResponse.from(
                productPage.getContent(),
                productPage.getTotalElements(),
                productPage.getNumber(),
                productPage.getTotalPages(),
                productPage.hasNext(),
                productPage.hasPrevious()
        );
    }

    // 상품명 검색
    public ProductDto.ProductListResponse searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByNameContainingAndIsActiveTrue(keyword, pageable);
        
        return ProductDto.ProductListResponse.from(
                productPage.getContent(),
                productPage.getTotalElements(),
                productPage.getNumber(),
                productPage.getTotalPages(),
                productPage.hasNext(),
                productPage.hasPrevious()
        );
    }

    // 카테고리별 상품명 검색
    public ProductDto.ProductListResponse searchProductsByCategory(String category, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByCategoryAndNameContainingAndIsActiveTrue(category, keyword, pageable);
        
        return ProductDto.ProductListResponse.from(
                productPage.getContent(),
                productPage.getTotalElements(),
                productPage.getNumber(),
                productPage.getTotalPages(),
                productPage.hasNext(),
                productPage.hasPrevious()
        );
    }

    // 모든 카테고리 조회
    public ProductDto.CategoryListResponse getAllCategories() {
        List<String> categories = productRepository.findAllCategories();
        return new ProductDto.CategoryListResponse(categories);
    }
} 