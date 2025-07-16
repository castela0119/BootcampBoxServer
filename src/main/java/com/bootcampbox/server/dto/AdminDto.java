package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDto {

    // === 사용자 관리 ===
    @Getter
    @Setter
    public static class UserListResponse {
        private List<UserInfo> users;
        private long totalUsers;
        private int currentPage;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public static UserListResponse from(List<User> users, long totalUsers, 
                                          int currentPage, int totalPages, 
                                          boolean hasNext, boolean hasPrevious) {
            UserListResponse dto = new UserListResponse();
            dto.setUsers(users.stream().map(UserInfo::from).collect(Collectors.toList()));
            dto.setTotalUsers(totalUsers);
            dto.setCurrentPage(currentPage);
            dto.setTotalPages(totalPages);
            dto.setHasNext(hasNext);
            dto.setHasPrevious(hasPrevious);
            return dto;
        }
    }

    @Getter
    @Setter
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String phoneNumber;
        private String userType;
        private String role;
        private LocalDateTime createdAt;
        private boolean active;

        public static UserInfo from(User user) {
            UserInfo dto = new UserInfo();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setUserType(user.getUserType());
            dto.setRole(user.getRole());
            dto.setCreatedAt(user.getCreatedAt());
            dto.setActive(user.isActive());
            return dto;
        }
    }

    @Getter
    @Setter
    public static class UpdateUserRequest {
        private String nickname;
        private String userType;
        private String role;
        private boolean isActive;
    }

    // === 게시글 관리 ===
    @Getter
    @Setter
    public static class PostListResponse {
        private List<PostInfo> posts;
        private long totalPosts;
        private int currentPage;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public static PostListResponse from(List<com.bootcampbox.server.domain.Post> posts, long totalPosts, 
                                          int currentPage, int totalPages, 
                                          boolean hasNext, boolean hasPrevious) {
            PostListResponse dto = new PostListResponse();
            dto.setPosts(posts.stream().map(PostInfo::from).collect(Collectors.toList()));
            dto.setTotalPosts(totalPosts);
            dto.setCurrentPage(currentPage);
            dto.setTotalPages(totalPages);
            dto.setHasNext(hasNext);
            dto.setHasPrevious(hasPrevious);
            return dto;
        }
    }

    @Getter
    @Setter
    public static class PostInfo {
        private Long id;
        private String title;
        private String content;
        private String authorNickname;
        private String authorUsername;
        private LocalDateTime createdAt;
        private int commentCount;

        public static PostInfo from(com.bootcampbox.server.domain.Post post) {
            PostInfo dto = new PostInfo();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setAuthorNickname(post.getUser().getNickname());
            dto.setAuthorUsername(post.getUser().getUsername());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setCommentCount(post.getComments().size());
            return dto;
        }
    }

    // === 댓글 관리 ===
    @Getter
    @Setter
    public static class CommentListResponse {
        private List<CommentInfo> comments;
        private long totalComments;
        private int currentPage;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public static CommentListResponse from(List<com.bootcampbox.server.domain.Comment> comments, long totalComments, 
                                             int currentPage, int totalPages, 
                                             boolean hasNext, boolean hasPrevious) {
            CommentListResponse dto = new CommentListResponse();
            dto.setComments(comments.stream().map(CommentInfo::from).collect(Collectors.toList()));
            dto.setTotalComments(totalComments);
            dto.setCurrentPage(currentPage);
            dto.setTotalPages(totalPages);
            dto.setHasNext(hasNext);
            dto.setHasPrevious(hasPrevious);
            return dto;
        }
    }

    @Getter
    @Setter
    public static class CommentInfo {
        private Long id;
        private String content;
        private String authorNickname;
        private String authorUsername;
        private String postTitle;
        private LocalDateTime createdAt;
        private int likeCount;
        private int reportCount;

        public static CommentInfo from(com.bootcampbox.server.domain.Comment comment) {
            CommentInfo dto = new CommentInfo();
            dto.setId(comment.getId());
            dto.setContent(comment.getContent());
            dto.setAuthorNickname(comment.getAuthorNickname());
            dto.setAuthorUsername(comment.getAuthorUsername());
            dto.setPostTitle(comment.getPost().getTitle());
            dto.setCreatedAt(comment.getCreatedAt());
            dto.setLikeCount(comment.getLikeCount());
            dto.setReportCount(comment.getReportCount());
            return dto;
        }
    }

    // === 상품 관리 ===
    @Getter
    @Setter
    public static class CreateProductRequest {
        private String name;
        private String category;
        private java.math.BigDecimal price;
        private String imageUrl;
        private String externalUrl;
        private String description;
    }

    @Getter
    @Setter
    public static class UpdateProductRequest {
        private String name;
        private String category;
        private java.math.BigDecimal price;
        private String imageUrl;
        private String externalUrl;
        private String description;
        private boolean isActive;
    }

    // === 통계 ===
    @Getter
    @Setter
    public static class DashboardStats {
        private long totalUsers;
        private long totalPosts;
        private long totalComments;
        private long totalProducts;
        private long todayNewUsers;
        private long todayNewPosts;
        private long todayNewComments;
        private long reportedComments;

        public DashboardStats(long totalUsers, long totalPosts, long totalComments, long totalProducts,
                            long todayNewUsers, long todayNewPosts, long todayNewComments, long reportedComments) {
            this.totalUsers = totalUsers;
            this.totalPosts = totalPosts;
            this.totalComments = totalComments;
            this.totalProducts = totalProducts;
            this.todayNewUsers = todayNewUsers;
            this.todayNewPosts = todayNewPosts;
            this.todayNewComments = todayNewComments;
            this.reportedComments = reportedComments;
        }
    }

    // === 공통 응답 ===
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

    // === 신고 관리 ===
    @Getter
    @Setter
    public static class PostReportListResponse {
        private List<PostReportInfo> reports;
        private long totalReports;
        private int currentPage;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public static PostReportListResponse from(List<com.bootcampbox.server.domain.PostReport> reports, long totalReports, 
                                                int currentPage, int totalPages, 
                                                boolean hasNext, boolean hasPrevious) {
            PostReportListResponse dto = new PostReportListResponse();
            dto.setReports(reports.stream().map(PostReportInfo::from).collect(Collectors.toList()));
            dto.setTotalReports(totalReports);
            dto.setCurrentPage(currentPage);
            dto.setTotalPages(totalPages);
            dto.setHasNext(hasNext);
            dto.setHasPrevious(hasPrevious);
            return dto;
        }
    }

    @Getter
    @Setter
    public static class PostReportInfo {
        private Long id;
        private Long postId;
        private String postTitle;
        private Long reporterId;
        private String reporterNickname;
        private String reportType;
        private String reportStatus;
        private String reason;
        private LocalDateTime createdAt;
        private LocalDateTime processedAt;

        public static PostReportInfo from(com.bootcampbox.server.domain.PostReport report) {
            PostReportInfo dto = new PostReportInfo();
            dto.setId(report.getId());
            dto.setPostId(report.getPost().getId());
            dto.setPostTitle(report.getPost().getTitle());
            dto.setReporterId(report.getUser().getId());
            dto.setReporterNickname(report.getUser().getNickname());
            dto.setReportType(report.getReportType().name());
            dto.setReportStatus(report.getStatus().name());
            dto.setReason(report.getAdditionalReason());
            dto.setCreatedAt(report.getCreatedAt());
            dto.setProcessedAt(report.getProcessedAt());
            return dto;
        }
    }

    @Getter
    @Setter
    public static class CommentReportListResponse {
        private List<CommentReportInfo> reports;
        private long totalReports;
        private int currentPage;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public static CommentReportListResponse from(List<com.bootcampbox.server.domain.CommentReport> reports, long totalReports, 
                                                   int currentPage, int totalPages, 
                                                   boolean hasNext, boolean hasPrevious) {
            CommentReportListResponse dto = new CommentReportListResponse();
            dto.setReports(reports.stream().map(CommentReportInfo::from).collect(Collectors.toList()));
            dto.setTotalReports(totalReports);
            dto.setCurrentPage(currentPage);
            dto.setTotalPages(totalPages);
            dto.setHasNext(hasNext);
            dto.setHasPrevious(hasPrevious);
            return dto;
        }
    }

    @Getter
    @Setter
    public static class CommentReportInfo {
        private Long id;
        private Long commentId;
        private String commentContent;
        private String postTitle;
        private Long reporterId;
        private String reporterNickname;
        private String reportType;
        private String reportStatus;
        private String reason;
        private LocalDateTime createdAt;
        private LocalDateTime processedAt;

        public static CommentReportInfo from(com.bootcampbox.server.domain.CommentReport report) {
            CommentReportInfo dto = new CommentReportInfo();
            dto.setId(report.getCommentId()); // 복합 기본키의 commentId 부분 사용
            dto.setCommentId(report.getComment().getId());
            dto.setCommentContent(report.getComment().getContent());
            dto.setPostTitle(report.getComment().getPost().getTitle());
            dto.setReporterId(report.getUser().getId());
            dto.setReporterNickname(report.getUser().getNickname());
            dto.setReportType(report.getReportType().name());
            dto.setReportStatus(report.getStatus().name());
            dto.setReason(report.getAdditionalReason());
            dto.setCreatedAt(report.getCreatedAt());
            dto.setProcessedAt(report.getProcessedAt());
            return dto;
        }
    }
} 