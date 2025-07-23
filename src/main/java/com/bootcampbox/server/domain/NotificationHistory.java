package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_history")
@Getter
@Setter
@NoArgsConstructor
public class NotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "notification_type", nullable = false)
    private String notificationType; // like, comment

    @Column(name = "target_type", nullable = false)
    private String targetType; // post, comment

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 팩토리 메서드
    public static NotificationHistory createLikeHistory(User recipient, User sender, Long targetId) {
        NotificationHistory history = new NotificationHistory();
        history.setRecipient(recipient);
        history.setSender(sender);
        history.setNotificationType("like");
        history.setTargetType("post");
        history.setTargetId(targetId);
        history.setSentAt(LocalDateTime.now());
        return history;
    }

    public static NotificationHistory createCommentLikeHistory(User recipient, User sender, Long targetId) {
        NotificationHistory history = new NotificationHistory();
        history.setRecipient(recipient);
        history.setSender(sender);
        history.setNotificationType("like");
        history.setTargetType("comment");
        history.setTargetId(targetId);
        history.setSentAt(LocalDateTime.now());
        return history;
    }

    public static NotificationHistory createCommentHistory(User recipient, User sender, Long targetId) {
        NotificationHistory history = new NotificationHistory();
        history.setRecipient(recipient);
        history.setSender(sender);
        history.setNotificationType("comment");
        history.setTargetType("post");
        history.setTargetId(targetId);
        history.setSentAt(LocalDateTime.now());
        return history;
    }
} 