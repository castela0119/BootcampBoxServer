package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.*;
import com.bootcampbox.server.dto.ReportDto;
import com.bootcampbox.server.repository.CommentReportRepository;
import com.bootcampbox.server.repository.PostReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        
        log.info("댓글 신고 생성 완료: commentId={}, userId={}", savedReport.getCommentId(), savedReport.getUserId());
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
    public ReportDto.CommentReportResponse processCommentReport(Long commentId, Long userId, Long adminId, ReportDto.ProcessReportRequest request) {
        log.info("댓글 신고 처리: commentId={}, userId={}, adminId={}, status={}", commentId, userId, adminId, request.getStatus());
        
        CommentReportId reportId = new CommentReportId(commentId, userId);
        CommentReport report = commentReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));
        
        User admin = userService.getUserEntity(adminId);
        
        // 신고 처리
        report.process(request.getStatus(), request.getAdminComment(), admin);
        CommentReport savedReport = commentReportRepository.save(report);
        
        log.info("댓글 신고 처리 완료: commentId={}, userId={}, status={}", savedReport.getCommentId(), savedReport.getUserId(), savedReport.getStatus());
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

    // === 신고 상세 조회 ===
    public ReportDto.PostReportDetailResponse getPostReportDetail(Long reportId) {
        PostReport report = postReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 신고를 찾을 수 없습니다."));
        
        return ReportDto.PostReportDetailResponse.from(report);
    }

    public ReportDto.CommentReportDetailResponse getCommentReportDetail(Long commentId, Long userId) {
        CommentReportId reportId = new CommentReportId(commentId, userId);
        CommentReport report = commentReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 신고를 찾을 수 없습니다."));
        
        return ReportDto.CommentReportDetailResponse.from(report);
    }

    // === 사용자별 신고 내역 조회 ===
    public ReportDto.UserReportHistoryResponse getUserReportHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // 게시글 신고 내역
        Page<PostReport> postReportPage = postReportRepository.findByUserId(userId, pageable);
        List<ReportDto.UserReportHistoryResponse.UserReportItem> postReports = postReportPage.getContent().stream()
                .map(report -> ReportDto.UserReportHistoryResponse.UserReportItem.builder()
                        .reportId(report.getId())
                        .reportType(report.getReportType().name())
                        .reportTypeDescription(report.getReportType().getDescription())
                        .additionalReason(report.getAdditionalReason())
                        .status(report.getStatus().name())
                        .statusDescription(report.getStatus().getDescription())
                        .adminComment(report.getAdminComment())
                        .createdAt(report.getCreatedAt())
                        .processedAt(report.getProcessedAt())
                        .targetTitle(report.getPost().getTitle())
                        .targetType("POST")
                        .build())
                .collect(Collectors.toList());

        // 댓글 신고 내역
        Page<CommentReport> commentReportPage = commentReportRepository.findByUserId(userId, pageable);
        List<ReportDto.UserReportHistoryResponse.UserReportItem> commentReports = commentReportPage.getContent().stream()
                .map(report -> ReportDto.UserReportHistoryResponse.UserReportItem.builder()
                        .reportId(report.getCommentId()) // 복합 기본키의 commentId 부분 사용
                        .reportType(report.getReportType().name())
                        .reportTypeDescription(report.getReportType().getDescription())
                        .additionalReason(report.getAdditionalReason())
                        .status(report.getStatus().name())
                        .statusDescription(report.getStatus().getDescription())
                        .adminComment(report.getAdminComment())
                        .createdAt(report.getCreatedAt())
                        .processedAt(report.getProcessedAt())
                        .targetTitle(report.getComment().getContent().length() > 50 ? 
                                report.getComment().getContent().substring(0, 50) + "..." : 
                                report.getComment().getContent())
                        .targetType("COMMENT")
                        .build())
                .collect(Collectors.toList());

        return ReportDto.UserReportHistoryResponse.builder()
                .postReports(postReports)
                .commentReports(commentReports)
                .totalPostReports(postReportPage.getTotalElements())
                .totalCommentReports(commentReportPage.getTotalElements())
                .currentPage(page)
                .totalPages(Math.max(postReportPage.getTotalPages(), commentReportPage.getTotalPages()))
                .hasNext(postReportPage.hasNext() || commentReportPage.hasNext())
                .hasPrevious(postReportPage.hasPrevious() || commentReportPage.hasPrevious())
                .build();
    }

    // === 신고 통계 조회 ===
    public ReportDto.ReportStatisticsResponse getReportStatistics() {
        // 전체 신고 수
        long totalPostReports = postReportRepository.count();
        long totalCommentReports = commentReportRepository.count();

        // 상태별 신고 수
        long pendingPostReports = postReportRepository.countByStatus(ReportStatus.PENDING);
        long pendingCommentReports = commentReportRepository.countByStatus(ReportStatus.PENDING);
        long processedPostReports = postReportRepository.countByStatusIn(Arrays.asList(ReportStatus.APPROVED, ReportStatus.REJECTED));
        long processedCommentReports = commentReportRepository.countByStatusIn(Arrays.asList(ReportStatus.APPROVED, ReportStatus.REJECTED));

        // 신고 유형별 통계
        Map<String, Long> postReportsByType = Arrays.stream(ReportType.values())
                .collect(Collectors.toMap(
                        ReportType::name,
                        type -> postReportRepository.countByReportType(type)
                ));

        Map<String, Long> commentReportsByType = Arrays.stream(ReportType.values())
                .collect(Collectors.toMap(
                        ReportType::name,
                        type -> commentReportRepository.countByReportType(type)
                ));

        // 상태별 통계
        Map<String, Long> postReportsByStatus = Arrays.stream(ReportStatus.values())
                .collect(Collectors.toMap(
                        ReportStatus::name,
                        status -> postReportRepository.countByStatus(status)
                ));

        Map<String, Long> commentReportsByStatus = Arrays.stream(ReportStatus.values())
                .collect(Collectors.toMap(
                        ReportStatus::name,
                        status -> commentReportRepository.countByStatus(status)
                ));

        // 최근 7일간 일별 신고 수
        List<ReportDto.ReportStatisticsResponse.DailyReportCount> dailyPostReports = getDailyReportCounts(postReportRepository);
        List<ReportDto.ReportStatisticsResponse.DailyReportCount> dailyCommentReports = getDailyReportCounts(commentReportRepository);

        return ReportDto.ReportStatisticsResponse.builder()
                .totalPostReports(totalPostReports)
                .totalCommentReports(totalCommentReports)
                .pendingPostReports(pendingPostReports)
                .pendingCommentReports(pendingCommentReports)
                .processedPostReports(processedPostReports)
                .processedCommentReports(processedCommentReports)
                .postReportsByType(postReportsByType)
                .commentReportsByType(commentReportsByType)
                .postReportsByStatus(postReportsByStatus)
                .commentReportsByStatus(commentReportsByStatus)
                .dailyPostReports(dailyPostReports)
                .dailyCommentReports(dailyCommentReports)
                .build();
    }

    private List<ReportDto.ReportStatisticsResponse.DailyReportCount> getDailyReportCounts(PostReportRepository repository) {
        LocalDate today = LocalDate.now();
        List<ReportDto.ReportStatisticsResponse.DailyReportCount> dailyCounts = new ArrayList<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            long count = repository.countByCreatedAtBetween(startOfDay, endOfDay);
            dailyCounts.add(ReportDto.ReportStatisticsResponse.DailyReportCount.builder()
                    .date(date.toString())
                    .count(count)
                    .build());
        }
        
        return dailyCounts;
    }

    private List<ReportDto.ReportStatisticsResponse.DailyReportCount> getDailyReportCounts(CommentReportRepository repository) {
        LocalDate today = LocalDate.now();
        List<ReportDto.ReportStatisticsResponse.DailyReportCount> dailyCounts = new ArrayList<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            long count = repository.countByCreatedAtBetween(startOfDay, endOfDay);
            dailyCounts.add(ReportDto.ReportStatisticsResponse.DailyReportCount.builder()
                    .date(date.toString())
                    .count(count)
                    .build());
        }
        
        return dailyCounts;
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