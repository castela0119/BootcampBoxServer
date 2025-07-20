package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(nullable = false)
    private String type; // comment, like, follow, system

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "target_type")
    private String targetType; // post, comment, user

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 알림 생성 팩토리 메서드들
    public static Notification createCommentNotification(User recipient, User sender, Long postId) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setSender(sender);
        notification.setType("comment");
        notification.setTitle("새 댓글");
        notification.setContent(sender.getNickname() + "님이 회원님의 게시글에 댓글을 남겼습니다.");
        notification.setTargetType("post");
        notification.setTargetId(postId);
        notification.setRead(false);
        return notification;
    }

    public static Notification createLikeNotification(User recipient, User sender, String targetType, Long targetId) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setSender(sender);
        notification.setType("like");
        notification.setTitle("좋아요");
        notification.setContent(sender.getNickname() + "님이 회원님의 " + 
            (targetType.equals("post") ? "게시글" : "댓글") + "에 좋아요를 눌렀습니다.");
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setRead(false);
        return notification;
    }

    public static Notification createSystemNotification(User recipient, String title, String content) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setSender(null);
        notification.setType("system");
        notification.setTitle(title);
        notification.setContent(content);
        notification.setTargetType("system");
        notification.setTargetId(null);
        notification.setRead(false);
        return notification;
    }
} 