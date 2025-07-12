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

        public static CommentResponse from(Comment comment, String currentUsername) {
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
            response.isAuthor = comment.getAuthorUsername().equals(currentUsername);
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

        public static CommentListResponse from(List<Comment> comments, long totalComments, 
                                             int currentPage, int totalPages, 
                                             boolean hasNext, boolean hasPrevious, 
                                             String currentUsername) {
            CommentListResponse response = new CommentListResponse();
            response.setComments(comments.stream()
                    .map(comment -> CommentResponse.from(comment, currentUsername))
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
} 