package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    private String fcmToken;

    private String role = "USER";

    @Column(nullable = false)
    private String userType; // 현역, 예비역, 곰신, 민간인, 기타

    private String zipcode;
    private String address;
    private String addressDetail;

    // 군 복무 정보
    @Column(name = "military_rank")
    private String militaryRank;
    private String branch;
    private LocalDate enlistDate;
    private LocalDate dischargeDate;
    private String unit;

    // 곰신 정보
    private String boyfriendRank;
    private String boyfriendBranch;
    private LocalDate boyfriendEnlistDate;
    private LocalDate boyfriendDischargeDate;
    private String boyfriendUnit;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // 계정 활성화 상태
    private boolean isActive = true;
    
    // 🆕 활동 통계 필드 추가
    @Column(name = "total_received_likes", nullable = false)
    private int totalReceivedLikes = 0;  // 받은 좋아요 총합
    
    @Column(name = "total_given_likes", nullable = false)
    private int totalGivenLikes = 0;     // 준 좋아요 총합
    
    @Column(name = "total_posts", nullable = false)
    private int totalPosts = 0;          // 작성한 게시글 수
    
    @Column(name = "total_comments", nullable = false)
    private int totalComments = 0;       // 작성한 댓글 수
    
    @Column(name = "total_bookmarks", nullable = false)
    private int totalBookmarks = 0;      // 북마크한 게시글 수
} 