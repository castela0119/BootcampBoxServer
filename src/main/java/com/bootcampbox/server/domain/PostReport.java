package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_reports", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"post_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class PostReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String reason; // 신고 사유

    private LocalDateTime createdAt = LocalDateTime.now();
} 