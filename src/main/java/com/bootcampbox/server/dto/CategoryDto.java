package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "카테고리명은 필수 입력값입니다.")
        private String name;
        
        private String description;
        
        @JsonProperty("isAnonymous")
        private boolean isAnonymous = false;
        
        private int sortOrder = 0;

        @Builder
        public CreateRequest(String name, String description, boolean isAnonymous, int sortOrder) {
            this.name = name;
            this.description = description;
            this.isAnonymous = isAnonymous;
            this.sortOrder = sortOrder;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "카테고리명은 필수 입력값입니다.")
        private String name;
        
        private String description;
        
        @JsonProperty("isAnonymous")
        private boolean isAnonymous;
        
        private int sortOrder;

        @Builder
        public UpdateRequest(String name, String description, boolean isAnonymous, int sortOrder) {
            this.name = name;
            this.description = description;
            this.isAnonymous = isAnonymous;
            this.sortOrder = sortOrder;
        }
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String englishName;
        private String description;
        
        @JsonProperty("isAnonymous")
        private boolean isAnonymous;
        
        private int sortOrder;
        
        @JsonProperty("isActive")
        private boolean isActive;
        
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Category category) {
            return Response.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .englishName(category.getEnglishName())
                    .description(category.getDescription())
                    .isAnonymous(category.isAnonymous())
                    .sortOrder(category.getSortOrder())
                    .isActive(category.isActive())
                    .createdAt(category.getCreatedAt())
                    .updatedAt(category.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ListResponse {
        private List<Response> categories;

        public static ListResponse from(List<Category> categories) {
            List<Response> categoryResponses = categories.stream()
                    .map(Response::from)
                    .collect(Collectors.toList());
            
            return ListResponse.builder()
                    .categories(categoryResponses)
                    .build();
        }
    }
} 