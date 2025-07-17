package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.PostDto;
import com.bootcampbox.server.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto.Response> createPost(
            @Valid @RequestBody PostDto.CreateRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("게시글 작성 요청: username={}, title={}", username, request.getTitle());
            PostDto.Response response = postService.createPost(username, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 작성 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto.Response> getPost(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("게시글 조회 요청: postId={}, username={}", postId, username);
            PostDto.Response response = postService.getPost(postId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<PostDto.Response>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String authorUserType,
            @RequestParam(required = false) String tags, // 복수 태그 (콤마 구분)
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        
        log.info("게시글 목록 조회 요청: page={}, size={}, search={}, authorUserType={}, tags={}, sortBy={}, sortOrder={}", 
                page, size, search, authorUserType, tags, sortBy, sortOrder);
        
        Page<PostDto.Response> response = postService.getAllPostsWithFilters(
                page, size, search, authorUserType, tags, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostDto.Response>> getPostsByUser(
            @PathVariable Long userId, 
            Pageable pageable) {
        log.info("사용자별 게시글 목록 조회 요청: userId={}, page={}, size={}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<PostDto.Response> response = postService.getPostsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto.Response> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostDto.UpdateRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("게시글 수정 요청: postId={}, username={}, title={}", postId, username, request.getTitle());
            PostDto.Response response = postService.updatePost(postId, username, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 수정 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("게시글 삭제 요청: postId={}, username={}", postId, username);
            postService.deletePost(postId, username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("게시글 삭제 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 태그 검색 관련 API들
    @GetMapping("/search/tag/{tagName}")
    public ResponseEntity<Page<PostDto.Response>> getPostsByTag(
            @PathVariable String tagName,
            Pageable pageable) {
        log.info("태그별 게시글 검색: tagName={}", tagName);
        Page<PostDto.Response> response = postService.getPostsByTag(tagName, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/tags")
    public ResponseEntity<Page<PostDto.Response>> getPostsByTags(
            @RequestParam List<String> tagNames,
            @RequestParam(defaultValue = "OR") String operator, // OR 또는 AND
            Pageable pageable) {
        log.info("다중 태그 게시글 검색: tagNames={}, operator={}", tagNames, operator);
        Page<PostDto.Response> response = postService.getPostsByTags(tagNames, operator, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<Page<PostDto.Response>> searchPostsByKeyword(
            @RequestParam String keyword,
            Pageable pageable) {
        log.info("키워드 게시글 검색: keyword={}", keyword);
        Page<PostDto.Response> response = postService.searchPostsByKeyword(keyword, pageable);
        return ResponseEntity.ok(response);
    }
} 