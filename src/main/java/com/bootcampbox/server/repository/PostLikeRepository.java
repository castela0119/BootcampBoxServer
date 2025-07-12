package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.PostLike;
import com.bootcampbox.server.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    // 특정 게시글의 좋아요 수 조회
    long countByPostId(Long postId);
    
    // 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
    
    // 사용자가 좋아요한 게시글 목록 조회
    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user.id = :userId")
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);
    
    // 특정 게시글의 좋아요한 사용자 목록 조회
    @Query("SELECT pl.user.id FROM PostLike pl WHERE pl.post.id = :postId")
    List<Long> findUserIdsByPostId(@Param("postId") Long postId);
    
    // 마이페이지 내 활동을 위한 메서드들
    Page<PostLike> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    long countByUser(User user);
    
    @Query("SELECT pl FROM PostLike pl WHERE pl.user = :user ORDER BY pl.createdAt DESC")
    Optional<PostLike> findTopByUserOrderByCreatedAtDesc(@Param("user") User user);
} 