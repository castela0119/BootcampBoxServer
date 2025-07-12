package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 게시글의 모든 댓글 조회 (부모 댓글만, 최신순)
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findByPostIdOrderByCreatedAtDesc(@Param("postId") Long postId, Pageable pageable);
    
    // 특정 댓글의 대댓글 조회 (최신순)
    @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId ORDER BY c.createdAt ASC")
    List<Comment> findByParentIdOrderByCreatedAtAsc(@Param("parentId") Long parentId);
    
    // 사용자가 작성한 댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.user.username = :username ORDER BY c.createdAt DESC")
    Page<Comment> findByUsernameOrderByCreatedAtDesc(@Param("username") String username, Pageable pageable);
    
    // 게시글의 댓글 수 조회
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);
    
    // 댓글 작성자 확인
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Comment c WHERE c.id = :commentId AND c.user.username = :username")
    boolean existsByIdAndUsername(@Param("commentId") Long commentId, @Param("username") String username);
    
    // 관리자 기능을 위한 메서드들
    Page<Comment> findByContentContaining(String content, Pageable pageable);
    Page<Comment> findByReportCountGreaterThan(int reportCount, Pageable pageable);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByReportCountGreaterThan(int reportCount);

    // 사용자가 작성한 댓글 수 조회 (User 엔티티로)
    long countByUser(com.bootcampbox.server.domain.User user);

    // 사용자가 작성한 댓글 수 조회 (userId로)
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId")
    long countByUser(@Param("userId") Long userId);
} 