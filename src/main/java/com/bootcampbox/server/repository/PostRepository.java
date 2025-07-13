package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.User;
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
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // 관리자 기능을 위한 메서드들
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // 마이페이지 내 활동을 위한 메서드들
    long countByUser(User user);
    
    @Query("SELECT p FROM Post p WHERE p.user = :user ORDER BY p.createdAt DESC, p.id DESC LIMIT 1")
    Optional<Post> findTopByUserOrderByCreatedAtDesc(@Param("user") User user);
    
    // 특정 사용자가 받은 좋아요 총합 계산
    @Query("SELECT COALESCE(SUM(p.likeCount), 0) FROM Post p WHERE p.user = :user")
    long sumLikeCountByUser(@Param("user") User user);
    
    // 또는 실제 post_likes 테이블에서 계산 (더 정확)
    @Query("SELECT COUNT(pl) FROM PostLike pl JOIN pl.post p WHERE p.user = :user")
    long countReceivedLikesByUser(@Param("user") User user);
    
    // 태그 검색 관련 메서드들
    // 특정 태그가 포함된 게시글 검색
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t.name = :tagName ORDER BY p.createdAt DESC")
    Page<Post> findByTagName(@Param("tagName") String tagName, Pageable pageable);
    
    // 여러 태그(AND)로 검색 (모든 태그가 포함된 게시글)
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name IN :tagNames GROUP BY p HAVING COUNT(DISTINCT t.name) = :tagCount ORDER BY p.createdAt DESC")
    Page<Post> findByAllTags(@Param("tagNames") List<String> tagNames, @Param("tagCount") long tagCount, Pageable pageable);
    
    // 여러 태그(OR)로 검색 (하나라도 포함된 게시글)
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t.name IN :tagNames ORDER BY p.createdAt DESC")
    Page<Post> findByAnyTags(@Param("tagNames") List<String> tagNames, Pageable pageable);
    
    // 제목, 내용, 태그로 통합 검색
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE " +
           "p.title LIKE %:keyword% OR p.content LIKE %:keyword% OR t.name LIKE %:keyword% " +
           "ORDER BY p.createdAt DESC")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
} 