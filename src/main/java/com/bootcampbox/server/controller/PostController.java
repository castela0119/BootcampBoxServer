package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.PostDto;
import com.bootcampbox.server.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto.Response> createPost(
            @RequestParam Long userId,
            @Valid @RequestBody PostDto.CreateRequest request) {
        log.info("게시글 작성 요청: userId={}, title={}", userId, request.getTitle());
        PostDto.Response response = postService.createPost(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto.Response> getPost(@PathVariable Long postId) {
        log.info("게시글 조회 요청: postId={}", postId);
        PostDto.Response response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PostDto.Response>> getAllPosts(Pageable pageable) {
        log.info("전체 게시글 목록 조회 요청: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<PostDto.Response> response = postService.getAllPosts(pageable);
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
            @RequestParam Long userId,
            @Valid @RequestBody PostDto.UpdateRequest request) {
        log.info("게시글 수정 요청: postId={}, userId={}, title={}", postId, userId, request.getTitle());
        PostDto.Response response = postService.updatePost(postId, userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        log.info("게시글 삭제 요청: postId={}, userId={}", postId, userId);
        postService.deletePost(postId, userId);
        return ResponseEntity.ok().build();
    }
} 