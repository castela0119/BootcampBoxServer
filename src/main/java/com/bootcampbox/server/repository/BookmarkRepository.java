package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.Bookmark;
import com.bootcampbox.server.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    // 사용자가 특정 게시글을 북마크했는지 확인
    Optional<Bookmark> findByPostIdAndUserId(Long postId, Long userId);
    
    // 사용자가 북마크한 게시글 목록 조회
    @Query("SELECT b.post.id FROM Bookmark b WHERE b.user.id = :userId")
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);
    
    // 특정 게시글의 북마크한 사용자 목록 조회
    @Query("SELECT b.user.id FROM Bookmark b WHERE b.post.id = :postId")
    List<Long> findUserIdsByPostId(@Param("postId") Long postId);
    
    // 마이페이지 내 활동을 위한 메서드들
    Page<Bookmark> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    long countByUser(User user);
    
    @Query("SELECT b FROM Bookmark b WHERE b.user = :user ORDER BY b.createdAt DESC")
    Optional<Bookmark> findTopByUserOrderByCreatedAtDesc(@Param("user") User user);
} 