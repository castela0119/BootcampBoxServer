package com.bootcampbox.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment_reports")
@Getter
@Setter
@NoArgsConstructor
@IdClass(CommentReportId.class)
public class CommentReport {
    @Id
    @Column(name = "comment_id")
    private Long commentId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
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

    // 복합 기본키를 위한 메서드들
    public CommentReportId getId() {
        return new CommentReportId(commentId, userId);
    }

    public void setId(CommentReportId id) {
        this.commentId = id.getCommentId();
        this.userId = id.getUserId();
    }

    // 신고 생성 메서드
    public static CommentReport createReport(Comment comment, User user, ReportType reportType, String additionalReason) {
        CommentReport report = new CommentReport();
        report.setCommentId(comment.getId());
        report.setUserId(user.getId());
        report.setComment(comment);
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