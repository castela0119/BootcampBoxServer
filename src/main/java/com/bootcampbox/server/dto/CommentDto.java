package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.Comment;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDto {

    @Getter
    @Setter
    public static class CreateCommentRequest {
        @NotBlank(message = "댓글 내용은 필수입니다.")
        private String content;
        
        private Long parentId; // 대댓글인 경우 부모 댓글 ID
    }

    @Getter
    @Setter
    public static class UpdateCommentRequest {
        @NotBlank(message = "댓글 내용은 필수입니다.")
        private String content;
    }

    @Getter
    @Setter
    public static class CommentResponse {
        private Long id;
        private String content;
        private String authorNickname;
        private String authorUsername;
        private Long authorId;
        private Long postId;
        private Long parentId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<CommentResponse> replies; // 대댓글 목록
        private boolean isAuthor; // 현재 사용자가 작성자인지 여부
        
        // 좋아요 관련 필드
        private int likeCount; // 좋아요 수
        private boolean isLiked; // 현재 사용자가 좋아요를 눌렀는지 여부

        public static CommentResponse from(Comment comment, Long currentUserId) {
            return from(comment, currentUserId, false);
        }

        public static CommentResponse from(Comment comment, Long currentUserId, boolean isLiked) {
            CommentResponse response = new CommentResponse();
            response.setId(comment.getId());
            response.setContent(comment.getContent());
            response.setAuthorNickname(comment.getAuthorNickname());
            response.setAuthorUsername(comment.getAuthorUsername());
            response.setAuthorId(comment.getAuthorId());
            response.setPostId(comment.getPost().getId());
            response.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
            response.setCreatedAt(comment.getCreatedAt());
            response.setUpdatedAt(comment.getUpdatedAt());
            
            // ID 기반 비교 (가장 안전)
            response.isAuthor = comment.getAuthorId() != null && 
                               currentUserId != null && 
                               comment.getAuthorId().equals(currentUserId);
            
            // 좋아요 관련 정보
            response.likeCount = comment.getLikeCount();
            response.isLiked = isLiked;
            
            return response;
        }
        
        // 대댓글 목록을 포함한 응답 생성
        public static CommentResponse fromWithReplies(Comment comment, List<Comment> replies, Long currentUserId) {
            return fromWithReplies(comment, replies, currentUserId, false);
        }
        
        public static CommentResponse fromWithReplies(Comment comment, List<Comment> replies, Long currentUserId, boolean isLiked) {
            CommentResponse response = from(comment, currentUserId, isLiked);
            if (replies != null && !replies.isEmpty()) {
                response.setReplies(replies.stream()
                    .map(reply -> from(reply, currentUserId, false)) // 대댓글의 좋아요 상태는 별도 처리 필요
                    .collect(Collectors.toList()));
            }
            return response;
        }
    }

    @Getter
    @Setter
    public static class CommentListResponse {
        private List<CommentResponse> comments;
        private long totalComments;
        private int currentPage;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        // 기본 생성자 추가
        public CommentListResponse() {
        }

        public CommentListResponse(List<CommentResponse> comments, long totalComments, 
                                 int currentPage, int totalPages, 
                                 boolean hasNext, boolean hasPrevious) {
            this.comments = comments;
            this.totalComments = totalComments;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }
        
        public static CommentListResponse from(List<Comment> comments, long totalComments, 
                                             int currentPage, int totalPages, 
                                             boolean hasNext, boolean hasPrevious, 
                                             Long currentUserId) {
            CommentListResponse response = new CommentListResponse();
            response.setComments(comments.stream()
                    .map(comment -> CommentResponse.from(comment, currentUserId))
                    .collect(Collectors.toList()));
            response.setTotalComments(totalComments);
            response.setCurrentPage(currentPage);
            response.setTotalPages(totalPages);
            response.setHasNext(hasNext);
            response.setHasPrevious(hasPrevious);
            return response;
        }
    }

    @Getter
    @Setter
    public static class SimpleResponse {
        private String message;
        private boolean success;
        
        public SimpleResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }
    }

    @Getter
    @Setter
    public static class CommentAuthorsResponse {
        private String message;
        private List<CommentAuthorInfo> authors;
        private boolean success;
        
        public CommentAuthorsResponse(String message, List<CommentAuthorInfo> authors, boolean success) {
            this.message = message;
            this.authors = authors;
            this.success = success;
        }
    }

    @Getter
    @Setter
    public static class CommentAuthorInfo {
        private Long id;
        private String username;
        private String nickname;
        private LocalDateTime firstCommentAt; // 해당 게시글에 첫 댓글을 단 시간
        
        public CommentAuthorInfo(Long id, String username, String nickname, LocalDateTime firstCommentAt) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
            this.firstCommentAt = firstCommentAt;
        }
    }
} 