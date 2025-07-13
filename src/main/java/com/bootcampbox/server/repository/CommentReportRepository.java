package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.CommentReport;
import com.bootcampbox.server.domain.ReportStatus;
import com.bootcampbox.server.domain.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    
    // 특정 댓글의 신고 목록 조회
    List<CommentReport> findByCommentIdOrderByCreatedAtDesc(Long commentId);
    
    // 특정 사용자가 신고한 댓글 목록 조회
    List<CommentReport> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 신고 상태별 조회
    List<CommentReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);
    
    // 신고 분류별 조회
    List<CommentReport> findByReportTypeOrderByCreatedAtDesc(ReportType reportType);
    
    // 특정 댓글의 특정 사용자 신고 확인
    Optional<CommentReport> findByCommentIdAndUserId(Long commentId, Long userId);
    
    // 특정 댓글의 신고 개수 조회
    long countByCommentId(Long commentId);
    
    // 특정 댓글의 특정 상태 신고 개수 조회
    long countByCommentIdAndStatus(Long commentId, ReportStatus status);
    
    // 대기중인 신고 개수 조회
    long countByStatus(ReportStatus status);
    
    // 페이지네이션을 위한 신고 목록 조회
    Page<CommentReport> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 상태별 페이지네이션 조회
    Page<CommentReport> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);
    
    // 신고 분류별 페이지네이션 조회
    Page<CommentReport> findByReportTypeOrderByCreatedAtDesc(ReportType reportType, Pageable pageable);
    
    // 복합 조건 검색
    @Query("SELECT cr FROM CommentReport cr WHERE " +
           "(:status IS NULL OR cr.status = :status) AND " +
           "(:reportType IS NULL OR cr.reportType = :reportType) " +
           "ORDER BY cr.createdAt DESC")
    Page<CommentReport> findByStatusAndReportType(
            @Param("status") ReportStatus status,
            @Param("reportType") ReportType reportType,
            Pageable pageable);
    
    // 신고 분류별 개수 조회
    long countByReportType(ReportType reportType);
} 