package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDto {
    
    @Getter
    @Setter
    public static class ProductResponse {
        private Long id;
        private String name;
        private String category;
        private BigDecimal price;
        private String imageUrl;
        private String externalUrl;
        private String description;
        private LocalDateTime createdAt;

        public static ProductResponse from(Product product) {
            ProductResponse dto = new ProductResponse();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setCategory(product.getCategory());
            dto.setPrice(product.getPrice());
            dto.setImageUrl(product.getImageUrl());
            dto.setExternalUrl(product.getExternalUrl());
            dto.setDescription(product.getDescription());
            dto.setCreatedAt(product.getCreatedAt());
            return dto;
        }
    }

    @Getter
    @Setter
    public static class ProductListResponse {
        private List<ProductResponse> products;
        private long totalProducts;
        private int currentPage;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public static ProductListResponse from(List<Product> products, long totalProducts, 
                                             int currentPage, int totalPages, 
                                             boolean hasNext, boolean hasPrevious) {
            ProductListResponse dto = new ProductListResponse();
            dto.setProducts(products.stream().map(ProductResponse::from).collect(Collectors.toList()));
            dto.setTotalProducts(totalProducts);
            dto.setCurrentPage(currentPage);
            dto.setTotalPages(totalPages);
            dto.setHasNext(hasNext);
            dto.setHasPrevious(hasPrevious);
            return dto;
        }
    }

    @Getter
    @Setter
    public static class CategoryListResponse {
        private List<String> categories;

        public CategoryListResponse(List<String> categories) {
            this.categories = categories;
        }
    }
} 