package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_mutes")
@Getter
@Setter
@NoArgsConstructor
public class PostMute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    private LocalDateTime createdAt = LocalDateTime.now();



    public PostMute(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    // 뮤트 정보를 Map으로 반환
    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", id);
        map.put("postId", postId);
        map.put("userId", userId);
        map.put("mutedAt", createdAt);
        return map;
    }
} 