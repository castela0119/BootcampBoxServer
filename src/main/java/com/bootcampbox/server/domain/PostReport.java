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

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType; // 신고 분류

    @Column(name = "additional_reason")
    private String additionalReason; // 추가 신고 사유 (선택사항)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status = ReportStatus.PENDING; // 신고 처리 상태

    @Column(name = "admin_comment")
    private String adminComment; // 관리자 처리 코멘트

    @ManyToOne @JoinColumn(name = "processed_by")
    private User processedBy; // 처리한 관리자

    @Column(name = "processed_at")
    private LocalDateTime processedAt; // 처리 시간

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 신고 생성 메서드
    public static PostReport createReport(Post post, User user, ReportType reportType, String additionalReason) {
        PostReport report = new PostReport();
        report.setPost(post);
        report.setUser(user);
        report.setReportType(reportType);
        report.setAdditionalReason(additionalReason);
        return report;
    }

    // 신고 처리 메서드
    public void process(ReportStatus status, String adminComment, User admin) {
        this.status = status;
        this.adminComment = adminComment;
        this.processedBy = admin;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
} 