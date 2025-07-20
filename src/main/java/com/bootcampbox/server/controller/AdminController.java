package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.AdminDto;
import com.bootcampbox.server.dto.HotPostDto;
import com.bootcampbox.server.service.AdminService;
import com.bootcampbox.server.service.HotPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final HotPostService hotPostService;

    // === 대시보드 ===
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDto.DashboardStats> getDashboardStats() {
        AdminDto.DashboardStats stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // === 사용자 관리 ===
    @GetMapping("/users")
    public ResponseEntity<AdminDto.UserListResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        AdminDto.UserListResponse response = adminService.getAllUsers(page, size, search);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<AdminDto.SimpleResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody AdminDto.UpdateUserRequest request) {
        
        AdminDto.SimpleResponse response = adminService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<AdminDto.SimpleResponse> deleteUser(@PathVariable Long userId) {
        AdminDto.SimpleResponse response = adminService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }

    // === 게시글 관리 ===
    @GetMapping("/posts")
    public ResponseEntity<AdminDto.PostListResponse> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        AdminDto.PostListResponse response = adminService.getAllPosts(page, size, search);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<AdminDto.SimpleResponse> deletePost(@PathVariable Long postId) {
        AdminDto.SimpleResponse response = adminService.deletePost(postId);
        return ResponseEntity.ok(response);
    }

    // === 댓글 관리 ===
    @GetMapping("/comments")
    public ResponseEntity<AdminDto.CommentListResponse> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        AdminDto.CommentListResponse response = adminService.getAllComments(page, size, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comments/reported")
    public ResponseEntity<AdminDto.CommentListResponse> getReportedComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        AdminDto.CommentListResponse response = adminService.getReportedComments(page, size);
        return ResponseEntity.ok(response);
    }

    // === 신고 관리 ===
    @GetMapping("/post-reports")
    public ResponseEntity<AdminDto.PostReportListResponse> getPostReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        
        AdminDto.PostReportListResponse response = adminService.getPostReports(page, size, status, type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comment-reports")
    public ResponseEntity<AdminDto.CommentReportListResponse> getCommentReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        
        AdminDto.CommentReportListResponse response = adminService.getCommentReports(page, size, status, type);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<AdminDto.SimpleResponse> deleteComment(@PathVariable Long commentId) {
        AdminDto.SimpleResponse response = adminService.deleteComment(commentId);
        return ResponseEntity.ok(response);
    }

    // === 상품 관리 ===
    @PostMapping("/products")
    public ResponseEntity<AdminDto.SimpleResponse> createProduct(
            @RequestBody AdminDto.CreateProductRequest request) {
        
        AdminDto.SimpleResponse response = adminService.createProduct(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<AdminDto.SimpleResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody AdminDto.UpdateProductRequest request) {
        
        AdminDto.SimpleResponse response = adminService.updateProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<AdminDto.SimpleResponse> deleteProduct(@PathVariable Long productId) {
        AdminDto.SimpleResponse response = adminService.deleteProduct(productId);
        return ResponseEntity.ok(response);
    }

    // === HOT 게시글 관리 ===
    @GetMapping("/hot-stats")
    public ResponseEntity<HotPostDto.HotStatsResponse> getHotStats() {
        try {
            log.info("HOT 게시글 통계 조회 요청");
            HotPostDto.HotStatsResponse response = hotPostService.getHotStats();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("HOT 게시글 통계 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/posts/{postId}/hot-score")
    public ResponseEntity<HotPostDto.HotScoreAdjustResponse> adjustHotScore(
            @PathVariable Long postId,
            @RequestBody HotPostDto.HotScoreAdjustRequest request) {
        try {
            log.info("HOT 점수 수동 조정 요청 - 게시글: {}, 새 점수: {}", postId, request.getHotScore());
            HotPostDto.HotScoreAdjustResponse response = hotPostService.adjustHotScore(postId, request.getHotScore());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("HOT 점수 수동 조정 오류: ", e);
            return ResponseEntity.badRequest().body(
                new HotPostDto.HotScoreAdjustResponse("HOT 점수 조정 실패: " + e.getMessage(), 0, 0, false, false)
            );
        }
    }
} 