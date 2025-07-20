package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_settings")
@Getter
@Setter
@NoArgsConstructor
public class NotificationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "comment_enabled", nullable = false)
    private boolean commentEnabled = true;

    @Column(name = "like_enabled", nullable = false)
    private boolean likeEnabled = true;

    @Column(name = "follow_enabled", nullable = false)
    private boolean followEnabled = true;

    @Column(name = "system_enabled", nullable = false)
    private boolean systemEnabled = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 기본 설정으로 생성하는 팩토리 메서드
    public static NotificationSettings createDefaultSettings(User user) {
        NotificationSettings settings = new NotificationSettings();
        settings.setUser(user);
        settings.setCommentEnabled(true);
        settings.setLikeEnabled(true);
        settings.setFollowEnabled(true);
        settings.setSystemEnabled(true);
        return settings;
    }

    // 설정 업데이트 메서드
    public void updateSettings(boolean commentEnabled, boolean likeEnabled, 
                              boolean followEnabled, boolean systemEnabled) {
        this.commentEnabled = commentEnabled;
        this.likeEnabled = likeEnabled;
        this.followEnabled = followEnabled;
        this.systemEnabled = systemEnabled;
        this.updatedAt = LocalDateTime.now();
    }

    // 특정 타입 설정 업데이트
    public void updateTypeSetting(String type, boolean enabled) {
        switch (type.toLowerCase()) {
            case "comment":
                this.commentEnabled = enabled;
                break;
            case "like":
                this.likeEnabled = enabled;
                break;
            case "follow":
                this.followEnabled = enabled;
                break;
            case "system":
                this.systemEnabled = enabled;
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 알림 타입: " + type);
        }
        this.updatedAt = LocalDateTime.now();
    }

    // 설정을 Map으로 반환
    public java.util.Map<String, Boolean> toMap() {
        java.util.Map<String, Boolean> settings = new java.util.HashMap<>();
        settings.put("comment", commentEnabled);
        settings.put("like", likeEnabled);
        settings.put("follow", followEnabled);
        settings.put("system", systemEnabled);
        return settings;
    }
} 