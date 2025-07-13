package com.bootcampbox.server.controller;

import com.bootcampbox.server.domain.ReportStatus;
import com.bootcampbox.server.domain.ReportType;
import com.bootcampbox.server.dto.ReportDto;
import com.bootcampbox.server.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    // 게시글 신고 생성
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ReportDto.PostReportResponse> createPostReport(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @Valid @RequestBody ReportDto.PostReportRequest request) {
        log.info("게시글 신고 생성 요청: postId={}, userId={}, reportType={}", postId, userId, request.getReportType());
        ReportDto.PostReportResponse response = reportService.createPostReport(postId, userId, request);
        return ResponseEntity.ok(response);
    }

    // 댓글 신고 생성
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<ReportDto.CommentReportResponse> createCommentReport(
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @Valid @RequestBody ReportDto.CommentReportRequest request) {
        log.info("댓글 신고 생성 요청: commentId={}, userId={}, reportType={}", commentId, userId, request.getReportType());
        ReportDto.CommentReportResponse response = reportService.createCommentReport(commentId, userId, request);
        return ResponseEntity.ok(response);
    }

    // 게시글 신고 처리 (관리자용)
    @PutMapping("/posts/{reportId}/process")
    public ResponseEntity<ReportDto.PostReportResponse> processPostReport(
            @PathVariable Long reportId,
            @RequestParam Long adminId,
            @Valid @RequestBody ReportDto.ProcessReportRequest request) {
        log.info("게시글 신고 처리 요청: reportId={}, adminId={}, status={}", reportId, adminId, request.getStatus());
        ReportDto.PostReportResponse response = reportService.processPostReport(reportId, adminId, request);
        return ResponseEntity.ok(response);
    }

    // 댓글 신고 처리 (관리자용)
    @PutMapping("/comments/{reportId}/process")
    public ResponseEntity<ReportDto.CommentReportResponse> processCommentReport(
            @PathVariable Long reportId,
            @RequestParam Long adminId,
            @Valid @RequestBody ReportDto.ProcessReportRequest request) {
        log.info("댓글 신고 처리 요청: reportId={}, adminId={}, status={}", reportId, adminId, request.getStatus());
        ReportDto.CommentReportResponse response = reportService.processCommentReport(reportId, adminId, request);
        return ResponseEntity.ok(response);
    }

    // 게시글 신고 목록 조회 (관리자용)
    @GetMapping("/posts")
    public ResponseEntity<Page<ReportDto.PostReportResponse>> getPostReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportType reportType,
            Pageable pageable) {
        log.info("게시글 신고 목록 조회 요청: status={}, reportType={}", status, reportType);
        Page<ReportDto.PostReportResponse> response = reportService.getPostReports(status, reportType, pageable);
        return ResponseEntity.ok(response);
    }

    // 댓글 신고 목록 조회 (관리자용)
    @GetMapping("/comments")
    public ResponseEntity<Page<ReportDto.CommentReportResponse>> getCommentReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportType reportType,
            Pageable pageable) {
        log.info("댓글 신고 목록 조회 요청: status={}, reportType={}", status, reportType);
        Page<ReportDto.CommentReportResponse> response = reportService.getCommentReports(status, reportType, pageable);
        return ResponseEntity.ok(response);
    }

    // 신고 통계 조회 (관리자용)
    @GetMapping("/statistics")
    public ResponseEntity<ReportDto.ReportStatisticsResponse> getReportStatistics() {
        log.info("신고 통계 조회 요청");
        ReportDto.ReportStatisticsResponse response = reportService.getReportStatistics();
        return ResponseEntity.ok(response);
    }

    // 신고 분류 목록 조회
    @GetMapping("/types")
    public ResponseEntity<ReportDto.ReportTypeListResponse> getReportTypes() {
        log.info("신고 분류 목록 조회 요청");
        ReportDto.ReportTypeListResponse response = reportService.getReportTypes();
        return ResponseEntity.ok(response);
    }

    // 특정 게시글의 신고 개수 조회
    @GetMapping("/posts/{postId}/count")
    public ResponseEntity<Long> getPostReportCount(@PathVariable Long postId) {
        log.info("게시글 신고 개수 조회 요청: postId={}", postId);
        long count = reportService.getPostReportCount(postId);
        return ResponseEntity.ok(count);
    }

    // 특정 댓글의 신고 개수 조회
    @GetMapping("/comments/{commentId}/count")
    public ResponseEntity<Long> getCommentReportCount(@PathVariable Long commentId) {
        log.info("댓글 신고 개수 조회 요청: commentId={}", commentId);
        long count = reportService.getCommentReportCount(commentId);
        return ResponseEntity.ok(count);
    }
} 