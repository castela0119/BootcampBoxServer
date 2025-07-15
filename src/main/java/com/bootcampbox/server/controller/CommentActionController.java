package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.CommentActionDto;
import com.bootcampbox.server.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentActionController {

    private final CommentService commentService;

    @PostMapping("/{commentId}/toggle-like")
    public ResponseEntity<CommentActionDto.ActionResponse> toggleCommentLike(@PathVariable Long commentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("댓글 좋아요 토글 요청 - 댓글: {}, 사용자: {}", commentId, username);
            CommentActionDto.ActionResponse response = commentService.toggleCommentLike(commentId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("댓글 좋아요 토글 오류: ", e);
            return ResponseEntity.badRequest().body(
                new CommentActionDto.ActionResponse("댓글 좋아요 토글 실패: " + e.getMessage(), 0, false)
            );
        }
    }

    // === 댓글 좋아요 (기존 방식 - 주석 처리) ===
    /*
    @PostMapping("/{commentId}/like")
    public ResponseEntity<CommentActionDto.ActionResponse> likeComment(@PathVariable Long commentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("댓글 좋아요 요청 - 댓글: {}, 사용자: {}", commentId, username);
            CommentActionDto.ActionResponse response = commentService.likeComment(commentId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("댓글 좋아요 오류: ", e);
            return ResponseEntity.badRequest().body(
                new CommentActionDto.ActionResponse("댓글 좋아요 실패: " + e.getMessage(), 0, false)
            );
        }
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<CommentActionDto.ActionResponse> unlikeComment(@PathVariable Long commentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("댓글 좋아요 취소 요청 - 댓글: {}, 사용자: {}", commentId, username);
            CommentActionDto.ActionResponse response = commentService.unlikeComment(commentId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("댓글 좋아요 취소 오류: ", e);
            return ResponseEntity.badRequest().body(
                new CommentActionDto.ActionResponse("댓글 좋아요 취소 실패: " + e.getMessage(), 0, false)
            );
        }
    }
    */

    @PostMapping("/{commentId}/report")
    public ResponseEntity<CommentActionDto.ActionResponse> reportComment(@PathVariable Long commentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("댓글 신고 요청 - 댓글: {}, 사용자: {}", commentId, username);
            CommentActionDto.ActionResponse response = commentService.reportComment(commentId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("댓글 신고 오류: ", e);
            return ResponseEntity.badRequest().body(
                new CommentActionDto.ActionResponse("댓글 신고 실패: " + e.getMessage(), 0, false)
            );
        }
    }
} 