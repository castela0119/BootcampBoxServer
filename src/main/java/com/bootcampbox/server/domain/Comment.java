package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent; // 대댓글을 위한 부모 댓글

    // 좋아요: 중복 방지 위해 Set 사용
    @ManyToMany
    @JoinTable(
        name = "comment_likes",
        joinColumns = @JoinColumn(name = "comment_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedUsers = new HashSet<>();

    // 신고 횟수(성능 및 관리자 쿼리용)
    @Column(nullable = false)
    private int reportCount = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 댓글 작성자 정보 (JSON 직렬화 시 사용)
    @Transient
    public String getAuthorNickname() {
        return user != null ? user.getNickname() : null;
    }

    @Transient
    public String getAuthorUsername() {
        return user != null ? user.getUsername() : null;
    }

    @Transient
    public Long getAuthorId() {
        return user != null ? user.getId() : null;
    }

    // 좋아요 추가
    public boolean like(User user) {
        return likedUsers.add(user);
    }
    // 좋아요 취소
    public boolean unlike(User user) {
        return likedUsers.remove(user);
    }
    // 좋아요 수
    public int getLikeCount() {
        return likedUsers.size();
    }
    public Set<User> getLikedUsers() {
        return likedUsers;
    }

    // 신고 수 (CommentReport 엔티티에서 관리)
    public int getReportCount() {
        return reportCount;
    }
    
    // 신고 수 업데이트 (CommentReport 서비스에서 호출)
    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }
} 