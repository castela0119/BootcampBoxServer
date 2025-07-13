package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.*;
import com.bootcampbox.server.dto.ReportDto;
import com.bootcampbox.server.repository.CommentReportRepository;
import com.bootcampbox.server.repository.PostReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReportService {

    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    // 게시글 신고 생성
    public ReportDto.PostReportResponse createPostReport(Long postId, Long userId, ReportDto.PostReportRequest request) {
        log.info("게시글 신고 생성: postId={}, userId={}, reportType={}", postId, userId, request.getReportType());
        
        // 게시글 존재 확인
        Post post = postService.getPostEntity(postId);
        
        // 사용자 존재 확인
        User user = userService.getUserEntity(userId);
        
        // 중복 신고 확인
        if (postReportRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new IllegalArgumentException("이미 신고한 게시글입니다.");
        }
        
        // 신고 생성
        PostReport report = PostReport.createReport(post, user, request.getReportType(), request.getAdditionalReason());
        PostReport savedReport = postReportRepository.save(report);
        
        log.info("게시글 신고 생성 완료: reportId={}", savedReport.getId());
        return ReportDto.PostReportResponse.from(savedReport);
    }

    // 댓글 신고 생성
    public ReportDto.CommentReportResponse createCommentReport(Long commentId, Long userId, ReportDto.CommentReportRequest request) {
        log.info("댓글 신고 생성: commentId={}, userId={}, reportType={}", commentId, userId, request.getReportType());
        
        // 댓글 존재 확인
        Comment comment = commentService.getCommentEntity(commentId);
        
        // 사용자 존재 확인
        User user = userService.getUserEntity(userId);
        
        // 중복 신고 확인
        if (commentReportRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new IllegalArgumentException("이미 신고한 댓글입니다.");
        }
        
        // 신고 생성
        CommentReport report = CommentReport.createReport(comment, user, request.getReportType(), request.getAdditionalReason());
        CommentReport savedReport = commentReportRepository.save(report);
        
        log.info("댓글 신고 생성 완료: reportId={}", savedReport.getId());
        return ReportDto.CommentReportResponse.from(savedReport);
    }

    // 게시글 신고 처리 (관리자용)
    public ReportDto.PostReportResponse processPostReport(Long reportId, Long adminId, ReportDto.ProcessReportRequest request) {
        log.info("게시글 신고 처리: reportId={}, adminId={}, status={}", reportId, adminId, request.getStatus());
        
        PostReport report = postReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));
        
        User admin = userService.getUserEntity(adminId);
        
        // 신고 처리
        report.process(request.getStatus(), request.getAdminComment(), admin);
        PostReport savedReport = postReportRepository.save(report);
        
        log.info("게시글 신고 처리 완료: reportId={}, status={}", savedReport.getId(), savedReport.getStatus());
        return ReportDto.PostReportResponse.from(savedReport);
    }

    // 댓글 신고 처리 (관리자용)
    public ReportDto.CommentReportResponse processCommentReport(Long reportId, Long adminId, ReportDto.ProcessReportRequest request) {
        log.info("댓글 신고 처리: reportId={}, adminId={}, status={}", reportId, adminId, request.getStatus());
        
        CommentReport report = commentReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));
        
        User admin = userService.getUserEntity(adminId);
        
        // 신고 처리
        report.process(request.getStatus(), request.getAdminComment(), admin);
        CommentReport savedReport = commentReportRepository.save(report);
        
        log.info("댓글 신고 처리 완료: reportId={}, status={}", savedReport.getId(), savedReport.getStatus());
        return ReportDto.CommentReportResponse.from(savedReport);
    }

    // 게시글 신고 목록 조회 (관리자용)
    public Page<ReportDto.PostReportResponse> getPostReports(ReportStatus status, ReportType reportType, Pageable pageable) {
        log.info("게시글 신고 목록 조회: status={}, reportType={}", status, reportType);
        
        Page<PostReport> reports;
        if (status != null && reportType != null) {
            reports = postReportRepository.findByStatusAndReportType(status, reportType, pageable);
        } else if (status != null) {
            reports = postReportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (reportType != null) {
            reports = postReportRepository.findByReportTypeOrderByCreatedAtDesc(reportType, pageable);
        } else {
            reports = postReportRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        return reports.map(ReportDto.PostReportResponse::from);
    }

    // 댓글 신고 목록 조회 (관리자용)
    public Page<ReportDto.CommentReportResponse> getCommentReports(ReportStatus status, ReportType reportType, Pageable pageable) {
        log.info("댓글 신고 목록 조회: status={}, reportType={}", status, reportType);
        
        Page<CommentReport> reports;
        if (status != null && reportType != null) {
            reports = commentReportRepository.findByStatusAndReportType(status, reportType, pageable);
        } else if (status != null) {
            reports = commentReportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (reportType != null) {
            reports = commentReportRepository.findByReportTypeOrderByCreatedAtDesc(reportType, pageable);
        } else {
            reports = commentReportRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        return reports.map(ReportDto.CommentReportResponse::from);
    }

    // 신고 통계 조회 (관리자용)
    public ReportDto.ReportStatisticsResponse getReportStatistics() {
        log.info("신고 통계 조회");
        
        return ReportDto.ReportStatisticsResponse.builder()
                .totalPendingReports(
                        postReportRepository.countByStatus(ReportStatus.PENDING) +
                        commentReportRepository.countByStatus(ReportStatus.PENDING)
                )
                .totalApprovedReports(
                        postReportRepository.countByStatus(ReportStatus.APPROVED) +
                        commentReportRepository.countByStatus(ReportStatus.APPROVED)
                )
                .totalRejectedReports(
                        postReportRepository.countByStatus(ReportStatus.REJECTED) +
                        commentReportRepository.countByStatus(ReportStatus.REJECTED)
                )
                .totalProcessingReports(
                        postReportRepository.countByStatus(ReportStatus.PROCESSING) +
                        commentReportRepository.countByStatus(ReportStatus.PROCESSING)
                )
                .commercialAdReports(
                        postReportRepository.countByReportType(ReportType.COMMERCIAL_AD) +
                        commentReportRepository.countByReportType(ReportType.COMMERCIAL_AD)
                )
                .abuseDiscriminationReports(
                        postReportRepository.countByReportType(ReportType.ABUSE_DISCRIMINATION) +
                        commentReportRepository.countByReportType(ReportType.ABUSE_DISCRIMINATION)
                )
                .pornographyInappropriateReports(
                        postReportRepository.countByReportType(ReportType.PORNOGRAPHY_INAPPROPRIATE) +
                        commentReportRepository.countByReportType(ReportType.PORNOGRAPHY_INAPPROPRIATE)
                )
                .leakImpersonationFraudReports(
                        postReportRepository.countByReportType(ReportType.LEAK_IMPERSONATION_FRAUD) +
                        commentReportRepository.countByReportType(ReportType.LEAK_IMPERSONATION_FRAUD)
                )
                .illegalVideoDistributionReports(
                        postReportRepository.countByReportType(ReportType.ILLEGAL_VIDEO_DISTRIBUTION) +
                        commentReportRepository.countByReportType(ReportType.ILLEGAL_VIDEO_DISTRIBUTION)
                )
                .inappropriateForBoardReports(
                        postReportRepository.countByReportType(ReportType.INAPPROPRIATE_FOR_BOARD) +
                        commentReportRepository.countByReportType(ReportType.INAPPROPRIATE_FOR_BOARD)
                )
                .trollingSpamReports(
                        postReportRepository.countByReportType(ReportType.TROLLING_SPAM) +
                        commentReportRepository.countByReportType(ReportType.TROLLING_SPAM)
                )
                .build();
    }

    // 신고 분류 목록 조회
    public ReportDto.ReportTypeListResponse getReportTypes() {
        return ReportDto.ReportTypeListResponse.from();
    }

    // 특정 게시글의 신고 개수 조회
    public long getPostReportCount(Long postId) {
        return postReportRepository.countByPostId(postId);
    }

    // 특정 댓글의 신고 개수 조회
    public long getCommentReportCount(Long commentId) {
        return commentReportRepository.countByCommentId(commentId);
    }
} 