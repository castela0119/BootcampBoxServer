package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.Tag;
import com.bootcampbox.server.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        private List<String> tagNames; // 태그명 목록
        private boolean isAnonymous = false; // 익명 여부
        private String authorUserType; // 작성 당시 사용자의 신분 type
        private Long categoryId; // 카테고리 ID

        @Builder
        public CreateRequest(String title, String content, List<String> tagNames, boolean isAnonymous, String authorUserType, Long categoryId) {
            this.title = title;
            this.content = content;
            this.tagNames = tagNames;
            this.isAnonymous = isAnonymous;
            this.authorUserType = authorUserType;
            this.categoryId = categoryId;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        private List<String> tagNames; // 태그명 목록
        private boolean isAnonymous = false; // 익명 여부
        private String authorUserType; // 작성 당시 사용자의 신분 type
        private Long categoryId; // 카테고리 ID

        @Builder
        public UpdateRequest(String title, String content, List<String> tagNames, boolean isAnonymous, String authorUserType, Long categoryId) {
            this.title = title;
            this.content = content;
            this.tagNames = tagNames;
            this.isAnonymous = isAnonymous;
            this.authorUserType = authorUserType;
            this.categoryId = categoryId;
        }
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private UserDto.SimpleUserResponse user;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // 익명 관련 필드
        @JsonProperty("isAnonymous")
        private boolean isAnonymous;
        private String anonymousNickname;
        private String displayNickname; // 표시할 닉네임 (익명이면 익명닉네임, 아니면 실제 닉네임)
        private boolean canBeModified; // 수정 가능 여부
        private boolean canBeDeleted; // 삭제 가능 여부
        private String authorUserType; // 작성 당시 사용자의 신분 type
        private List<String> tagNames; // 태그명 목록
        
        // 카테고리 관련 필드
        private Long categoryId; // 카테고리 ID
        private String categoryName; // 카테고리명
        
        // 좋아요 관련 필드
        private int likeCount; // 좋아요 수
        private boolean isLiked; // 현재 사용자가 좋아요를 눌렀는지 여부
        private boolean isBookmarked; // 현재 사용자가 북마크했는지 여부
        private int commentCount; // 댓글 수
        private int viewCount; // 조회수

        public static Response from(Post post) {
            return from(post, null, false, false);
        }

        public static Response from(Post post, Long currentUserId, boolean isLiked, boolean isBookmarked) {
            String displayNickname = post.isAnonymous() ? 
                post.getAnonymousNickname() : 
                post.getUser().getNickname();
                
            List<String> tagNames = post.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());

            return Response.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .user(post.isAnonymous() ? null : UserDto.SimpleUserResponse.from(post.getUser())) // 익명이면 작성자 정보 숨김
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .isAnonymous(post.isAnonymous())
                    .anonymousNickname(post.getAnonymousNickname())
                    .displayNickname(displayNickname)
                    .canBeModified(post.canBeModified())
                    .canBeDeleted(post.canBeDeleted())
                    .authorUserType(post.getAuthorUserType()) // 작성 당시 신분 type
                    .tagNames(tagNames)
                    .categoryId(post.getCategory() != null ? post.getCategory().getId() : null)
                    .categoryName(post.getCategory() != null ? post.getCategory().getName() : null)
                    .likeCount(post.getLikeCount())
                    .isLiked(isLiked)
                    .isBookmarked(isBookmarked)
                    .commentCount(post.getCommentCount())
                    .viewCount(post.getViewCount())
                    .build();
        }
    }

    // 카테고리별 검색 결과 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySearchResponse {
        private List<Response> posts;
        private PaginationInfo pagination;
        private SearchInfo searchInfo;
        
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class PaginationInfo {
            private int currentPage;
            private int totalPages;
            private long totalElements;
            private int size;
            private boolean hasNext;
            private boolean hasPrevious;
        }
        
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SearchInfo {
            private String searchKeyword;
            private String category;
            private int resultCount;
        }
    }
} 