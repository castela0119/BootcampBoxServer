package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.CommentActionDto;
import com.bootcampbox.server.dto.CommentDto;
import com.bootcampbox.server.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    
    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDto.CommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentDto.CreateCommentRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("댓글 작성 요청 - 게시글: {}, 사용자: {}", postId, username);
            CommentDto.CommentResponse response = commentService.createComment(postId, request, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("댓글 작성 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<CommentDto.CommentListResponse> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("댓글 목록 조회 요청 - 게시글: {}, 페이지: {}, 크기: {}, 사용자: {}", postId, page, size, username);
            CommentDto.CommentListResponse response = commentService.getComments(postId, page, size, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("댓글 목록 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto.CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDto.UpdateCommentRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("댓글 수정 요청 - 댓글: {}, 사용자: {}", commentId, username);
            CommentDto.CommentResponse response = commentService.updateComment(commentId, request, username);
            
            // 영속성 컨텍스트 정리
            entityManager.flush();
            entityManager.clear();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("댓글 수정 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto.SimpleResponse> deleteComment(@PathVariable Long commentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("댓글 삭제 요청 - 댓글: {}, 사용자: {}", commentId, username);
            CommentDto.SimpleResponse response = commentService.deleteComment(commentId, username);
            
            // 영속성 컨텍스트 정리
            entityManager.flush();
            entityManager.clear();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("댓글 삭제 오류: ", e);
            return ResponseEntity.badRequest().body(
                new CommentDto.SimpleResponse("댓글 삭제 실패: " + e.getMessage(), false)
            );
        }
    }

    @GetMapping("/user/me/comments")
    public ResponseEntity<Page<CommentDto.CommentResponse>> getMyComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("내 댓글 목록 조회 요청 - 사용자: {}, 페이지: {}, 크기: {}", username, page, size);
            Page<CommentDto.CommentResponse> response = commentService.getMyComments(username, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내 댓글 목록 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글의 댓글 작성자들 조회
    @GetMapping("/{postId}/comment-authors")
    public ResponseEntity<CommentDto.CommentAuthorsResponse> getCommentAuthors(@PathVariable Long postId) {
        try {
            log.info("게시글 댓글 작성자 조회 요청 - 게시글: {}", postId);
            CommentDto.CommentAuthorsResponse response = commentService.getCommentAuthors(postId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 댓글 작성자 조회 오류: ", e);
            return ResponseEntity.badRequest().body(
                new CommentDto.CommentAuthorsResponse("댓글 작성자 조회 실패: " + e.getMessage(), null, false)
            );
        }
    }

    @GetMapping("/comments/{commentId}/likes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentActionDto.UserListResponse> getCommentLikes(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getCommentLikes(commentId));
    }

    @GetMapping("/comments/{commentId}/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentActionDto.UserListResponse> getCommentReports(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getCommentReports(commentId));
    }

    @DeleteMapping("/comments/{commentId}/report/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentActionDto.ActionResponse> unreportComment(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        CommentActionDto.ActionResponse response = commentService.unreportComment(commentId, userId);
        return ResponseEntity.ok(response);
    }


} 