package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.PostMute;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PostMuteDto {
    
    @Getter
    @Setter
    public static class MuteResponse {
        private Long postId;
        private Long userId;
        private LocalDateTime mutedAt;
        
        public static MuteResponse from(PostMute postMute) {
            MuteResponse response = new MuteResponse();
            response.setPostId(postMute.getPostId());
            response.setUserId(postMute.getUserId());
            response.setMutedAt(postMute.getCreatedAt());
            return response;
        }
    }
    
    @Getter
    @Setter
    public static class UnmuteResponse {
        private Long postId;
        private Long userId;
        private LocalDateTime unmutedAt;
        
        public UnmuteResponse(Long postId, Long userId) {
            this.postId = postId;
            this.userId = userId;
            this.unmutedAt = LocalDateTime.now();
        }
    }
    
    @Getter
    @Setter
    public static class MuteStatusResponse {
        private boolean isMuted;
        private LocalDateTime mutedAt;
        
        public static MuteStatusResponse muted(LocalDateTime mutedAt) {
            MuteStatusResponse response = new MuteStatusResponse();
            response.isMuted = true;
            response.mutedAt = mutedAt;
            return response;
        }
        
        public static MuteStatusResponse notMuted() {
            MuteStatusResponse response = new MuteStatusResponse();
            response.isMuted = false;
            response.mutedAt = null;
            return response;
        }
    }
    
    @Getter
    @Setter
    public static class MutedPostInfo {
        private Long id;
        private Long postId;
        private String postTitle;
        private String postAuthor;
        private LocalDateTime mutedAt;
        
        public static MutedPostInfo from(PostMute postMute, String postTitle, String postAuthor) {
            MutedPostInfo info = new MutedPostInfo();
            info.setId(postMute.getId());
            info.setPostId(postMute.getPostId());
            info.setPostTitle(postTitle);
            info.setPostAuthor(postAuthor);
            info.setMutedAt(postMute.getCreatedAt());
            return info;
        }
    }
    
    @Getter
    @Setter
    public static class MutedPostsResponse {
        private List<MutedPostInfo> mutedPosts;
        private PaginationInfo pagination;
        
        public static MutedPostsResponse of(List<MutedPostInfo> mutedPosts, PaginationInfo pagination) {
            MutedPostsResponse response = new MutedPostsResponse();
            response.setMutedPosts(mutedPosts);
            response.setPagination(pagination);
            return response;
        }
    }
    
    @Getter
    @Setter
    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private boolean hasNext;
        
        public static PaginationInfo from(int currentPage, int totalPages, long totalElements, boolean hasNext) {
            PaginationInfo info = new PaginationInfo();
            info.setCurrentPage(currentPage);
            info.setTotalPages(totalPages);
            info.setTotalElements(totalElements);
            info.setHasNext(hasNext);
            return info;
        }
    }
    
    @Getter
    @Setter
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        private Object error;
        
        public static ApiResponse success(String message, Object data) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(true);
            response.setMessage(message);
            response.setData(data);
            response.setError(null);
            return response;
        }
        
        public static ApiResponse error(String message, Object error) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(false);
            response.setMessage(message);
            response.setData(null);
            response.setError(error);
            return response;
        }
    }
} 