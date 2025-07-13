package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    // 태그명으로 찾기
    Optional<Tag> findByName(String name);
    
    // 태그명 존재 여부 확인
    boolean existsByName(String name);
    
    // 태그 타입별 조회
    List<Tag> findByType(String type);
    
    // 태그명으로 검색 (부분 일치)
    List<Tag> findByNameContainingIgnoreCase(String name);
    
    // 인기 태그 조회 (게시글 수 기준)
    @Query("SELECT t FROM Tag t JOIN t.posts p GROUP BY t ORDER BY COUNT(p) DESC")
    List<Tag> findPopularTags();
    
    // 특정 게시글에 연결된 태그들 조회
    @Query("SELECT t FROM Tag t JOIN t.posts p WHERE p.id = :postId")
    List<Tag> findByPostId(@Param("postId") Long postId);
    
    // 태그명 목록으로 태그들 조회
    List<Tag> findByNameIn(List<String> names);
} 