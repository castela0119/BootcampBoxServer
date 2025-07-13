package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 익명 관련 필드
    @Column(name = "is_anonymous")
    private boolean isAnonymous = false; // 익명 여부 (true: 익명, false: 실명)
    
    @Column(name = "anonymous_nickname")
    private String anonymousNickname; // 익명일 때 표시할 닉네임
    
    @Column(name = "author_user_type")
    private String authorUserType; // 작성 당시 사용자의 신분 type

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // 댓글 목록
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
    
    // 좋아요, 신고, 북마크, 댓글 관련 필드
    private int likeCount = 0;
    private int reportCount = 0;
    private int viewCount = 0; // 조회수
    private int commentCount = 0; // 댓글 수
    
    // 좋아요, 신고, 북마크 관계
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> likes = new ArrayList<>();
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostReport> reports = new ArrayList<>();
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bookmark> bookmarks = new ArrayList<>();
    
    // 태그 관계
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    
    // 댓글 수 계산 메서드 (DB 컬럼 우선, 없으면 실시간 계산)
    public int getCommentCount() {
        return commentCount > 0 ? commentCount : comments.size();
    }
    
    // 댓글 수 업데이트 메서드
    public void updateCommentCount() {
        this.commentCount = comments.size();
    }
    
    // 익명 게시글인지 확인하는 메서드
    public boolean isAnonymousPost() {
        return isAnonymous;
    }
    
    // 익명 게시글 수정 가능 여부 확인 메서드
    public boolean canBeModified() {
        return !isAnonymous; // 익명 게시글은 수정 불가
    }
    
    // 익명 게시글 삭제 가능 여부 확인 메서드 (삭제는 가능)
    public boolean canBeDeleted() {
        return true; // 익명 게시글도 삭제는 가능
    }
    
    // 태그 추가 메서드
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }
    
    // 태그 제거 메서드
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }
    
    // 태그 목록 조회 메서드
    public Set<Tag> getTags() {
        return tags;
    }
} 