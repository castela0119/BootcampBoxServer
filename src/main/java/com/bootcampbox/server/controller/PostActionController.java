package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.PostActionDto;
import com.bootcampbox.server.service.PostActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostActionController {

    private final PostActionService postActionService;

    // === 게시글 좋아요 토글 ===
    @PostMapping("/{postId}/toggle-like")
    public ResponseEntity<PostActionDto.ActionResponse> togglePostLike(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("게시글 좋아요 토글 요청 - 게시글: {}, 사용자: {}", postId, username);
            PostActionDto.ActionResponse response = postActionService.togglePostLike(postId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 좋아요 토글 오류: ", e);
            return ResponseEntity.badRequest().body(
                new PostActionDto.ActionResponse("게시글 좋아요 토글 실패: " + e.getMessage(), 0, false, false, false)
            );
        }
    }



    // === 게시글 신고 ===
    @PostMapping("/{postId}/report")
    public ResponseEntity<PostActionDto.ActionResponse> reportPost(
            @PathVariable Long postId,
            @RequestBody PostActionDto.ReportRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("게시글 신고 요청 - 게시글: {}, 사용자: {}, 분류: {}", postId, username, request.getReportType());
            PostActionDto.ActionResponse response = postActionService.reportPost(postId, username, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 신고 오류: ", e);
            return ResponseEntity.badRequest().body(
                new PostActionDto.ActionResponse("게시글 신고 실패: " + e.getMessage(), 0, false, false, false)
            );
        }
    }

    // === 게시글 북마크 ===
    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<PostActionDto.ActionResponse> bookmarkPost(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("게시글 북마크 요청 - 게시글: {}, 사용자: {}", postId, username);
            PostActionDto.ActionResponse response = postActionService.bookmarkPost(postId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 북마크 오류: ", e);
            return ResponseEntity.badRequest().body(
                new PostActionDto.ActionResponse("게시글 북마크 실패: " + e.getMessage(), 0, false, false, false)
            );
        }
    }

    @DeleteMapping("/{postId}/bookmark")
    public ResponseEntity<PostActionDto.ActionResponse> unbookmarkPost(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("게시글 북마크 취소 요청 - 게시글: {}, 사용자: {}", postId, username);
            PostActionDto.ActionResponse response = postActionService.unbookmarkPost(postId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 북마크 취소 오류: ", e);
            return ResponseEntity.badRequest().body(
                new PostActionDto.ActionResponse("게시글 북마크 취소 실패: " + e.getMessage(), 0, false, false, false)
            );
        }
    }

    // === 내가 좋아요/북마크한 게시글 목록 ===
    @GetMapping("/user/me/likes")
    public ResponseEntity<PostActionDto.UserListResponse> getMyLikedPosts() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("내가 좋아요한 게시글 목록 조회 요청 - 사용자: {}", username);
            var postIds = postActionService.getMyLikedPosts(username);
            PostActionDto.UserListResponse response = new PostActionDto.UserListResponse(postIds, postIds.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내가 좋아요한 게시글 목록 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/me/bookmarks")
    public ResponseEntity<PostActionDto.UserListResponse> getMyBookmarkedPosts() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("내가 북마크한 게시글 목록 조회 요청 - 사용자: {}", username);
            var postIds = postActionService.getMyBookmarkedPosts(username);
            PostActionDto.UserListResponse response = new PostActionDto.UserListResponse(postIds, postIds.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내가 북마크한 게시글 목록 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // === 관리자용 API ===
    @GetMapping("/{postId}/likes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostActionDto.UserListResponse> getPostLikes(@PathVariable Long postId) {
        return ResponseEntity.ok(postActionService.getPostLikes(postId));
    }

    @GetMapping("/{postId}/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostActionDto.UserListResponse> getPostReports(@PathVariable Long postId) {
        return ResponseEntity.ok(postActionService.getPostReports(postId));
    }

    @DeleteMapping("/{postId}/report/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostActionDto.ActionResponse> unreportPost(
            @PathVariable Long postId,
            @PathVariable Long userId) {
        PostActionDto.ActionResponse response = postActionService.unreportPost(postId, userId);
        return ResponseEntity.ok(response);
    }
} 