package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MyPageDto {

    @Getter
    @Setter
    public static class UserInfoResponse {
        private Long id;
        private String username;
        private String phoneNumber;
        private String nickname;
        private String userType;
        private String role;
        private String zipcode;
        private String address;
        private String addressDetail;
        
        // 군 복무 정보
        private LocalDate enlistDate;
        private LocalDate dischargeDate;
        
        // 곰신 정보
        private LocalDate boyfriendEnlistDate;
        private LocalDate boyfriendDischargeDate;
        
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // 🆕 활동 통계 데이터 추가
        private int postCount;
        private int commentCount;
        private int total_received_likes;         // receivedLikesCount → total_received_likes로 변경
        private int bookmarkCount;
        private LocalDateTime lastActivityAt;

        public static UserInfoResponse from(User user) {
            UserInfoResponse response = new UserInfoResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setPhoneNumber(user.getPhoneNumber());
            response.setNickname(user.getNickname());
            response.setUserType(user.getUserType());
            response.setRole(user.getRole());
            response.setZipcode(user.getZipcode());
            response.setAddress(user.getAddress());
            response.setAddressDetail(user.getAddressDetail());
            response.setEnlistDate(user.getEnlistDate());
            response.setDischargeDate(user.getDischargeDate());
            response.setBoyfriendEnlistDate(user.getBoyfriendEnlistDate());
            response.setBoyfriendDischargeDate(user.getBoyfriendDischargeDate());
            response.setCreatedAt(user.getCreatedAt());
            response.setUpdatedAt(user.getUpdatedAt());
            
            // 🆕 활동 통계는 별도로 설정 (User 엔티티에는 없음)
            return response;
        }
    }

    @Getter
    @Setter
    public static class UpdateUserRequest {
        private String nickname;
        private String zipcode;
        private String address;
        private String addressDetail;
        
        // 군 복무 정보
        private LocalDate enlistDate;
        private LocalDate dischargeDate;
        
        // 곰신 정보
        private LocalDate boyfriendEnlistDate;
        private LocalDate boyfriendDischargeDate;
    }

    @Getter
    @Setter
    public static class UpdateUserResponse {
        private String message;
        private boolean success;
        private UserInfoResponse userInfo;

        public UpdateUserResponse(String message, boolean success, UserInfoResponse userInfo) {
            this.message = message;
            this.success = success;
            this.userInfo = userInfo;
        }
    }

    // 내가 쓴 글 응답 DTO
    @Getter
    @Setter
    public static class MyPostResponse {
        private Long id;
        private String title;
        private String content;
        private List<String> tagNames;
        private int likeCount;
        private int commentCount;
        private int viewCount;
        private LocalDateTime createdAt;
        private String authorNickname;
        private String authorUserType; // 작성 당시 사용자의 신분 type

        public static MyPostResponse from(Post post) {
            MyPostResponse response = new MyPostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setTagNames(post.getTags().stream()
                    .map(tag -> tag.getName())
                    .collect(Collectors.toList()));
            response.setLikeCount(post.getLikeCount());
            response.setCommentCount(post.getCommentCount());
            response.setViewCount(post.getViewCount());
            response.setCreatedAt(post.getCreatedAt());
            response.setAuthorNickname(post.getUser().getNickname());
            response.setAuthorUserType(post.getAuthorUserType()); // 작성 당시 신분 type
            return response;
        }
    }

    // 내가 좋아요한 글 응답 DTO
    @Getter
    @Setter
    public static class MyLikedPostResponse {
        private Long id;
        private String title;
        private String content;
        private List<String> tagNames;
        private int likeCount;
        private int commentCount;
        private int viewCount;
        private LocalDateTime createdAt;
        private String authorNickname;
        private String authorUserType; // 작성 당시 사용자의 신분 type
        private LocalDateTime likedAt; // 좋아요한 시간

        public static MyLikedPostResponse from(Post post, LocalDateTime likedAt) {
            MyLikedPostResponse response = new MyLikedPostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setTagNames(post.getTags().stream()
                    .map(tag -> tag.getName())
                    .collect(Collectors.toList()));
            response.setLikeCount(post.getLikeCount());
            response.setCommentCount(post.getCommentCount());
            response.setViewCount(post.getViewCount());
            response.setCreatedAt(post.getCreatedAt());
            response.setAuthorNickname(post.getUser().getNickname());
            response.setAuthorUserType(post.getAuthorUserType()); // 작성 당시 신분 type
            response.setLikedAt(likedAt);
            return response;
        }
    }

    // 내가 북마크한 글 응답 DTO
    @Getter
    @Setter
    public static class MyBookmarkedPostResponse {
        private Long id;
        private String title;
        private String content;
        private List<String> tagNames;
        private int likeCount;
        private int commentCount;
        private int viewCount;
        private LocalDateTime createdAt;
        private String authorNickname;
        private String authorUserType; // 작성 당시 사용자의 신분 type
        private LocalDateTime bookmarkedAt; // 북마크한 시간

        public static MyBookmarkedPostResponse from(Post post, LocalDateTime bookmarkedAt) {
            MyBookmarkedPostResponse response = new MyBookmarkedPostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setTagNames(post.getTags().stream()
                    .map(tag -> tag.getName())
                    .collect(Collectors.toList()));
            response.setLikeCount(post.getLikeCount());
            response.setCommentCount(post.getCommentCount());
            response.setViewCount(post.getViewCount());
            response.setCreatedAt(post.getCreatedAt());
            response.setAuthorNickname(post.getUser().getNickname());
            response.setAuthorUserType(post.getAuthorUserType()); // 작성 당시 신분 type
            response.setBookmarkedAt(bookmarkedAt);
            return response;
        }
    }

    // 내 활동 요약 응답 DTO
    @Getter
    @Setter
    public static class MyActivitySummaryResponse {
        private int totalPosts;
        private int totalComments;
        private int totalLikes;
        private int totalBookmarks;
        private LocalDateTime lastActivityAt;

        public MyActivitySummaryResponse(int totalPosts, int totalComments, int totalLikes, int totalBookmarks, LocalDateTime lastActivityAt) {
            this.totalPosts = totalPosts;
            this.totalComments = totalComments;
            this.totalLikes = totalLikes;
            this.totalBookmarks = totalBookmarks;
            this.lastActivityAt = lastActivityAt;
        }
    }
} 