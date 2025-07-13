package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.CommentReport;
import com.bootcampbox.server.domain.PostReport;
import com.bootcampbox.server.domain.ReportStatus;
import com.bootcampbox.server.domain.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReportDto {

    // 게시글 신고 요청
    @Getter
    @NoArgsConstructor
    public static class PostReportRequest {
        @NotNull(message = "신고 분류는 필수입니다.")
        private ReportType reportType;
        
        private String additionalReason; // 추가 신고 사유 (선택사항)

        @Builder
        public PostReportRequest(ReportType reportType, String additionalReason) {
            this.reportType = reportType;
            this.additionalReason = additionalReason;
        }
    }

    // 댓글 신고 요청
    @Getter
    @NoArgsConstructor
    public static class CommentReportRequest {
        @NotNull(message = "신고 분류는 필수입니다.")
        private ReportType reportType;
        
        private String additionalReason; // 추가 신고 사유 (선택사항)

        @Builder
        public CommentReportRequest(ReportType reportType, String additionalReason) {
            this.reportType = reportType;
            this.additionalReason = additionalReason;
        }
    }

    // 신고 처리 요청 (관리자용)
    @Getter
    @NoArgsConstructor
    public static class ProcessReportRequest {
        @NotNull(message = "처리 상태는 필수입니다.")
        private ReportStatus status;
        
        private String adminComment; // 관리자 코멘트

        @Builder
        public ProcessReportRequest(ReportStatus status, String adminComment) {
            this.status = status;
            this.adminComment = adminComment;
        }
    }

    // 게시글 신고 응답
    @Getter
    @Builder
    public static class PostReportResponse {
        private Long id;
        private Long postId;
        private String postTitle;
        private UserDto.SimpleUserResponse reporter; // 신고자
        private ReportType reportType;
        private String additionalReason;
        private ReportStatus status;
        private String adminComment;
        private UserDto.SimpleUserResponse processedBy; // 처리한 관리자
        private LocalDateTime processedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static PostReportResponse from(PostReport report) {
            return PostReportResponse.builder()
                    .id(report.getId())
                    .postId(report.getPost().getId())
                    .postTitle(report.getPost().getTitle())
                    .reporter(UserDto.SimpleUserResponse.from(report.getUser()))
                    .reportType(report.getReportType())
                    .additionalReason(report.getAdditionalReason())
                    .status(report.getStatus())
                    .adminComment(report.getAdminComment())
                    .processedBy(report.getProcessedBy() != null ? UserDto.SimpleUserResponse.from(report.getProcessedBy()) : null)
                    .processedAt(report.getProcessedAt())
                    .createdAt(report.getCreatedAt())
                    .updatedAt(report.getUpdatedAt())
                    .build();
        }
    }

    // 댓글 신고 응답
    @Getter
    @Builder
    public static class CommentReportResponse {
        private Long id;
        private Long commentId;
        private String commentContent;
        private Long postId;
        private String postTitle;
        private UserDto.SimpleUserResponse reporter; // 신고자
        private ReportType reportType;
        private String additionalReason;
        private ReportStatus status;
        private String adminComment;
        private UserDto.SimpleUserResponse processedBy; // 처리한 관리자
        private LocalDateTime processedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static CommentReportResponse from(CommentReport report) {
            return CommentReportResponse.builder()
                    .id(report.getId())
                    .commentId(report.getComment().getId())
                    .commentContent(report.getComment().getContent())
                    .postId(report.getComment().getPost().getId())
                    .postTitle(report.getComment().getPost().getTitle())
                    .reporter(UserDto.SimpleUserResponse.from(report.getUser()))
                    .reportType(report.getReportType())
                    .additionalReason(report.getAdditionalReason())
                    .status(report.getStatus())
                    .adminComment(report.getAdminComment())
                    .processedBy(report.getProcessedBy() != null ? UserDto.SimpleUserResponse.from(report.getProcessedBy()) : null)
                    .processedAt(report.getProcessedAt())
                    .createdAt(report.getCreatedAt())
                    .updatedAt(report.getUpdatedAt())
                    .build();
        }
    }

    // 신고 통계 응답
    @Getter
    @Builder
    public static class ReportStatisticsResponse {
        private long totalPendingReports;
        private long totalApprovedReports;
        private long totalRejectedReports;
        private long totalProcessingReports;
        
        // 분류별 통계
        private long commercialAdReports;
        private long abuseDiscriminationReports;
        private long pornographyInappropriateReports;
        private long leakImpersonationFraudReports;
        private long illegalVideoDistributionReports;
        private long inappropriateForBoardReports;
        private long trollingSpamReports;
    }

    // 신고 분류 목록 응답
    @Getter
    @Builder
    public static class ReportTypeListResponse {
        private List<ReportTypeItem> reportTypes;

        @Getter
        @Builder
        public static class ReportTypeItem {
            private String code;
            private String description;
        }

        public static ReportTypeListResponse from() {
            List<ReportTypeItem> items = List.of(
                    ReportTypeItem.builder().code("COMMERCIAL_AD").description("상업적 광고").build(),
                    ReportTypeItem.builder().code("ABUSE_DISCRIMINATION").description("욕설/비하").build(),
                    ReportTypeItem.builder().code("PORNOGRAPHY_INAPPROPRIATE").description("음란물/불건전 대화").build(),
                    ReportTypeItem.builder().code("LEAK_IMPERSONATION_FRAUD").description("유출/사칭/사기").build(),
                    ReportTypeItem.builder().code("ILLEGAL_VIDEO_DISTRIBUTION").description("불법촬영물 유통").build(),
                    ReportTypeItem.builder().code("INAPPROPRIATE_FOR_BOARD").description("게시판 성격에 부적절").build(),
                    ReportTypeItem.builder().code("TROLLING_SPAM").description("낚시/놀람/도배").build()
            );

            return ReportTypeListResponse.builder()
                    .reportTypes(items)
                    .build();
        }
    }
} 