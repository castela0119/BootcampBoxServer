package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HotPostDto {
    
    @Getter
    @Setter
    public static class HotPostResponse {
        private Long id;
        private String title;
        private String content;
        private String authorNickname;
        private String authorUsername;
        private Long authorId;
        private boolean isAnonymous;
        private String anonymousNickname;
        private String authorUserType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int likeCount;
        private int commentCount;
        private int viewCount;
        private int hotScore;
        private boolean isHot;
        private LocalDateTime hotUpdatedAt;
        private List<String> tagNames;
        
        public static HotPostResponse from(Post post) {
            HotPostResponse response = new HotPostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setAuthorNickname(post.getUser().getNickname());
            response.setAuthorUsername(post.getUser().getUsername());
            response.setAuthorId(post.getUser().getId());
            response.setAnonymous(post.isAnonymous());
            response.setAnonymousNickname(post.getAnonymousNickname());
            response.setAuthorUserType(post.getAuthorUserType());
            response.setCreatedAt(post.getCreatedAt());
            response.setUpdatedAt(post.getUpdatedAt());
            response.setLikeCount(post.getLikeCount());
            response.setCommentCount(post.getCommentCount());
            response.setViewCount(post.getViewCount());
            response.setHotScore(post.getHotScore());
            response.setHot(post.getIsHot());
            response.setHotUpdatedAt(post.getHotUpdatedAt());
            response.setTagNames(post.getTags().stream()
                    .map(tag -> tag.getName())
                    .collect(Collectors.toList()));
            return response;
        }
    }
    
    @Getter
    @Setter
    public static class HotPostListResponse {
        private List<HotPostResponse> content;
        private long totalElements;
        private int currentPage;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
        
        public HotPostListResponse(List<HotPostResponse> content, long totalElements, 
                                 int currentPage, int totalPages, boolean hasNext, boolean hasPrevious) {
            this.content = content;
            this.totalElements = totalElements;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }
    }
    
    @Getter
    @Setter
    public static class HotStatsResponse {
        private long totalHotPosts;
        private long todayHotPosts;
        private double averageHotScore;
        private double todayAverageHotScore;
        private LocalDateTime lastUpdated;
        
        public HotStatsResponse(long totalHotPosts, long todayHotPosts, 
                              double averageHotScore, double todayAverageHotScore, 
                              LocalDateTime lastUpdated) {
            this.totalHotPosts = totalHotPosts;
            this.todayHotPosts = todayHotPosts;
            this.averageHotScore = averageHotScore;
            this.todayAverageHotScore = todayAverageHotScore;
            this.lastUpdated = lastUpdated;
        }
    }
    
    @Getter
    @Setter
    public static class HotScoreAdjustRequest {
        private int hotScore;
        
        public HotScoreAdjustRequest() {}
        
        public HotScoreAdjustRequest(int hotScore) {
            this.hotScore = hotScore;
        }
    }
    
    @Getter
    @Setter
    public static class HotScoreAdjustResponse {
        private String message;
        private int oldHotScore;
        private int newHotScore;
        private boolean isHot;
        private boolean success;
        
        public HotScoreAdjustResponse(String message, int oldHotScore, int newHotScore, 
                                    boolean isHot, boolean success) {
            this.message = message;
            this.oldHotScore = oldHotScore;
            this.newHotScore = newHotScore;
            this.isHot = isHot;
            this.success = success;
        }
    }
} 