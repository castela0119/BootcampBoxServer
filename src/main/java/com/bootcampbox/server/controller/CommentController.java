package com.bootcampbox.server.controller;

import com.bootcampbox.server.config.CurrentUser;
import com.bootcampbox.server.dto.ApiResponse;
import com.bootcampbox.server.dto.CommentActionDto;
import com.bootcampbox.server.dto.CommentDto;
import com.bootcampbox.server.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<ApiResponse<CommentDto.CommentResponse>> createComment(
            @PathVariable Long postId,
            @CurrentUser String username,
            @Valid @RequestBody CommentDto.CreateCommentRequest request) {
        log.info("댓글 작성 요청 - 게시글: {}, 사용자: {}", postId, username);
        CommentDto.CommentResponse response = commentService.createComment(postId, request, username);
        return ResponseEntity.ok(ApiResponse.success("댓글이 성공적으로 작성되었습니다.", response));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentDto.CommentListResponse>> getComments(
            @PathVariable Long postId,
            @CurrentUser String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("댓글 목록 조회 요청 - 게시글: {}, 페이지: {}, 크기: {}, 사용자: {}", postId, page, size, username);
        CommentDto.CommentListResponse response = commentService.getComments(postId, page, size, username);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentDto.CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @CurrentUser String username,
            @Valid @RequestBody CommentDto.UpdateCommentRequest request) {
        log.info("댓글 수정 요청 - 댓글: {}, 사용자: {}", commentId, username);
        CommentDto.CommentResponse response = commentService.updateComment(commentId, request, username);
        
        // 영속성 컨텍스트 정리
        entityManager.flush();
        entityManager.clear();
        
        return ResponseEntity.ok(ApiResponse.success("댓글이 성공적으로 수정되었습니다.", response));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentDto.SimpleResponse>> deleteComment(
            @PathVariable Long commentId,
            @CurrentUser String username) {
        log.info("댓글 삭제 요청 - 댓글: {}, 사용자: {}", commentId, username);
        CommentDto.SimpleResponse response = commentService.deleteComment(commentId, username);
        
        // 영속성 컨텍스트 정리
        entityManager.flush();
        entityManager.clear();
        
        return ResponseEntity.ok(ApiResponse.success("댓글이 성공적으로 삭제되었습니다.", response));
    }

    @GetMapping("/user/me/comments")
    public ResponseEntity<ApiResponse<Page<CommentDto.CommentResponse>>> getMyComments(
            @CurrentUser String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("내 댓글 목록 조회 요청 - 사용자: {}, 페이지: {}, 크기: {}", username, page, size);
        Page<CommentDto.CommentResponse> response = commentService.getMyComments(username, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 게시글의 댓글 작성자들 조회
    @GetMapping("/{postId}/comment-authors")
    public ResponseEntity<ApiResponse<CommentDto.CommentAuthorsResponse>> getCommentAuthors(@PathVariable Long postId) {
        log.info("게시글 댓글 작성자 조회 요청 - 게시글: {}", postId);
        CommentDto.CommentAuthorsResponse response = commentService.getCommentAuthors(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/comments/{commentId}/likes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CommentActionDto.UserListResponse>> getCommentLikes(@PathVariable Long commentId) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getCommentLikes(commentId)));
    }

    @GetMapping("/comments/{commentId}/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CommentActionDto.UserListResponse>> getCommentReports(@PathVariable Long commentId) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getCommentReports(commentId)));
    }

    @DeleteMapping("/comments/{commentId}/report/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CommentActionDto.ActionResponse>> unreportComment(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        CommentActionDto.ActionResponse response = commentService.unreportComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글 신고가 취소되었습니다.", response));
    }


} 