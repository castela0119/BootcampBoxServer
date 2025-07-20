package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.PostMute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostMuteRepository extends JpaRepository<PostMute, Long> {
    
    // 사용자와 게시글 ID로 뮤트 정보 조회
    Optional<PostMute> findByUserIdAndPostId(Long userId, Long postId);
    
    // 사용자와 게시글 ID로 뮤트 존재 여부 확인
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    
    // 사용자별 뮤트된 게시글 목록 조회 (페이징)
    Page<PostMute> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 사용자와 게시글 ID로 뮤트 삭제
    @Modifying
    @Query("DELETE FROM PostMute pm WHERE pm.userId = :userId AND pm.postId = :postId")
    void deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
    
    // 사용자별 뮤트된 게시글 수 조회
    long countByUserId(Long userId);
} 