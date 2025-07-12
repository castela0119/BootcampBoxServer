package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
} 