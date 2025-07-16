package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.PostReport;
import com.bootcampbox.server.domain.ReportStatus;
import com.bootcampbox.server.domain.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    
    // 특정 게시글의 신고 수 조회
    long countByPostId(Long postId);
    
    // 사용자가 특정 게시글을 신고했는지 확인
    Optional<PostReport> findByPostIdAndUserId(Long postId, Long userId);
    
    // 사용자가 신고한 게시글 목록 조회
    @Query("SELECT pr.post.id FROM PostReport pr WHERE pr.user.id = :userId")
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);
    
    // 특정 게시글의 신고한 사용자 목록 조회
    @Query("SELECT pr.user.id FROM PostReport pr WHERE pr.post.id = :postId")
    List<Long> findUserIdsByPostId(@Param("postId") Long postId);
    
    // 특정 게시글의 신고 목록 조회
    List<PostReport> findByPostIdOrderByCreatedAtDesc(Long postId);
    
    // 특정 사용자가 신고한 게시글 목록 조회
    List<PostReport> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 신고 상태별 조회
    List<PostReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);
    
    // 신고 분류별 조회
    List<PostReport> findByReportTypeOrderByCreatedAtDesc(ReportType reportType);
    
    // 특정 게시글의 특정 상태 신고 개수 조회
    long countByPostIdAndStatus(Long postId, ReportStatus status);
    
    // 대기중인 신고 개수 조회
    long countByStatus(ReportStatus status);
    
    // 페이지네이션을 위한 신고 목록 조회
    Page<PostReport> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 상태별 페이지네이션 조회
    Page<PostReport> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);
    
    // 신고 분류별 페이지네이션 조회
    Page<PostReport> findByReportTypeOrderByCreatedAtDesc(ReportType reportType, Pageable pageable);
    
    // 복합 조건 검색
    @Query("SELECT pr FROM PostReport pr WHERE " +
           "(:status IS NULL OR pr.status = :status) AND " +
           "(:reportType IS NULL OR pr.reportType = :reportType) " +
           "ORDER BY pr.createdAt DESC")
    Page<PostReport> findByStatusAndReportType(
            @Param("status") ReportStatus status,
            @Param("reportType") ReportType reportType,
            Pageable pageable);
    
    // 신고 분류별 개수 조회
    long countByReportType(ReportType reportType);
    
    // 사용자별 신고 내역 조회
    Page<PostReport> findByUserId(Long userId, Pageable pageable);
    
    // 기간별 신고 수 조회
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // 상태 목록별 신고 수 조회
    long countByStatusIn(List<ReportStatus> statuses);
} 