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

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
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
    
    // 제목, 내용, 태그로 통합 검색 (AND 조건)
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE " +
           "p.title LIKE %:keyword% AND p.content LIKE %:keyword% " +
           "ORDER BY p.createdAt DESC")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 제목, 내용, 태그로 통합 검색 (OR 조건 - 기존 방식)
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE " +
           "p.title LIKE %:keyword% OR p.content LIKE %:keyword% OR t.name LIKE %:keyword% " +
           "ORDER BY p.createdAt DESC")
    Page<Post> searchByKeywordOr(@Param("keyword") String keyword, Pageable pageable);

    // 게시글 필터링을 위한 메서드들
    Page<Post> findByAuthorUserType(String authorUserType, Pageable pageable);
    
    // 복합 필터링 (검색 OR 조건 + 작성자 타입 + 태그)
    @Query(value = "SELECT p.* FROM posts p " +
           "LEFT JOIN users a ON p.user_id = a.id " +
           "LEFT JOIN post_tags pt ON p.id = pt.post_id " +
           "LEFT JOIN tags t ON pt.tag_id = t.id " +
           "WHERE (:search IS NULL OR (LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')))) " +
           "AND (:authorUserType IS NULL OR a.user_type = :authorUserType) " +
           "GROUP BY p.id " +
           "HAVING (:tagCount IS NULL OR COUNT(DISTINCT CASE WHEN t.name IN (:tagList) THEN t.name END) = :tagCount) order by p.created_at desc",
           countQuery = "SELECT COUNT(DISTINCT p.id) FROM posts p " +
           "LEFT JOIN users a ON p.user_id = a.id " +
           "LEFT JOIN post_tags pt ON p.id = pt.post_id " +
           "LEFT JOIN tags t ON pt.tag_id = t.id " +
           "WHERE (:search IS NULL OR (LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')))) " +
           "AND (:authorUserType IS NULL OR a.user_type = :authorUserType) " +
           "GROUP BY p.id " +
           "HAVING (:tagCount IS NULL OR COUNT(DISTINCT CASE WHEN t.name IN (:tagList) THEN t.name END) = :tagCount)",
           nativeQuery = true)
    Page<Post> findPostsWithFilters(
            @Param("search") String search,
            @Param("authorUserType") String authorUserType,
            @Param("tags") String tags,
            @Param("tagList") List<String> tagList,
            @Param("tagCount") Long tagCount,
            Pageable pageable);
    
    // 인기순 정렬을 위한 메서드들
    Page<Post> findAllByOrderByLikeCountDesc(Pageable pageable);
    Page<Post> findAllByOrderByViewCountDesc(Pageable pageable);
    Page<Post> findAllByOrderByCommentCountDesc(Pageable pageable);
    
    // HOT 게시글 관련 메서드들
    @Query("SELECT p FROM Post p WHERE p.isHot = true ORDER BY p.hotScore DESC, p.createdAt DESC")
    Page<Post> findHotPostsOrderByHotScoreDesc(Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.isHot = true AND p.createdAt >= :startDate ORDER BY p.hotScore DESC, p.createdAt DESC")
    Page<Post> findHotPostsByPeriodOrderByHotScoreDesc(@Param("startDate") LocalDateTime startDate, Pageable pageable);
    
    // 최근 7일 내 게시글 조회 (배치 작업용)
    @Query("SELECT p FROM Post p WHERE p.createdAt >= :startDate ORDER BY p.createdAt DESC")
    Page<Post> findRecentPostsForBatch(@Param("startDate") LocalDateTime startDate, Pageable pageable);
    
    // 오래된 HOT 게시글 조회 (7일 이상)
    @Query("SELECT p FROM Post p WHERE p.isHot = true AND p.createdAt < :cutoffDate")
    List<Post> findOldHotPosts(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // HOT 게시글 통계
    @Query("SELECT COUNT(p) FROM Post p WHERE p.isHot = true")
    long countHotPosts();
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.isHot = true AND p.createdAt >= :startDate")
    long countHotPostsByPeriod(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT AVG(p.hotScore) FROM Post p WHERE p.isHot = true")
    Double getAverageHotScore();
    
    @Query("SELECT AVG(p.hotScore) FROM Post p WHERE p.isHot = true AND p.createdAt >= :startDate")
    Double getAverageHotScoreByPeriod(@Param("startDate") LocalDateTime startDate);

    // ===== 카테고리별 게시글 조회 메서드 =====

    // 카테고리명으로 게시글 조회
    @Query("SELECT p FROM Post p JOIN p.category c WHERE c.name = :categoryName ORDER BY p.createdAt DESC")
    Page<Post> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    // 카테고리 ID로 게시글 조회
    @Query("SELECT p FROM Post p JOIN p.category c WHERE c.id = :categoryId ORDER BY p.createdAt DESC")
    Page<Post> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // 영문 카테고리명으로 게시글 조회
    @Query("SELECT p FROM Post p JOIN p.category c WHERE c.englishName = :englishName ORDER BY p.createdAt DESC")
    Page<Post> findByEnglishCategoryName(@Param("englishName") String englishName, Pageable pageable);

    // 카테고리별 HOT 게시글 조회
    @Query("SELECT p FROM Post p JOIN p.category c WHERE c.id = :categoryId AND p.isHot = true ORDER BY p.hotScore DESC, p.createdAt DESC")
    Page<Post> findHotPostsByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // 카테고리별 게시글 수 조회
    @Query("SELECT COUNT(p) FROM Post p JOIN p.category c WHERE c.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);
} 