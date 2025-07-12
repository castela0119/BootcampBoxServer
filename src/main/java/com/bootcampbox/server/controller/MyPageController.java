package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.MyPageDto;
import com.bootcampbox.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<MyPageDto.UserInfoResponse> getMyInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // JWT에서 email 추출
            
            log.info("내 정보 조회 요청: {}", email);
            MyPageDto.UserInfoResponse response = userService.getUserInfo(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내 정보 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/me")
    public ResponseEntity<MyPageDto.UpdateUserResponse> updateMyInfo(
            @RequestBody MyPageDto.UpdateUserRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            log.info("내 정보 수정 요청: {}", email);
            MyPageDto.UpdateUserResponse response = userService.updateUserInfo(email, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내 정보 수정 오류: ", e);
            return ResponseEntity.badRequest().body(
                new MyPageDto.UpdateUserResponse("정보 수정 실패: " + e.getMessage(), false, null)
            );
        }
    }

    // 내가 쓴 글 목록 조회
    @GetMapping("/me/posts")
    public ResponseEntity<Page<MyPageDto.MyPostResponse>> getMyPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            log.info("내가 쓴 글 목록 조회 요청: {}", email);
            Page<MyPageDto.MyPostResponse> response = userService.getMyPosts(email, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내가 쓴 글 목록 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 내가 좋아요한 글 목록 조회
    @GetMapping("/me/likes")
    public ResponseEntity<Page<MyPageDto.MyLikedPostResponse>> getMyLikedPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            log.info("내가 좋아요한 글 목록 조회 요청: {}", email);
            Page<MyPageDto.MyLikedPostResponse> response = userService.getMyLikedPosts(email, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내가 좋아요한 글 목록 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 내가 북마크한 글 목록 조회
    @GetMapping("/me/bookmarks")
    public ResponseEntity<Page<MyPageDto.MyBookmarkedPostResponse>> getMyBookmarkedPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            log.info("내가 북마크한 글 목록 조회 요청: {}", email);
            Page<MyPageDto.MyBookmarkedPostResponse> response = userService.getMyBookmarkedPosts(email, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내가 북마크한 글 목록 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 내 활동 요약 조회
    @GetMapping("/me/activity-summary")
    public ResponseEntity<MyPageDto.MyActivitySummaryResponse> getMyActivitySummary() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            log.info("내 활동 요약 조회 요청: {}", email);
            MyPageDto.MyActivitySummaryResponse response = userService.getMyActivitySummary(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내 활동 요약 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }
} 