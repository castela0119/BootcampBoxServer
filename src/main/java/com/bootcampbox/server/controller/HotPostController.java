package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.HotPostDto;
import com.bootcampbox.server.service.HotPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class HotPostController {

    private final HotPostService hotPostService;

    /**
     * HOT 게시글 목록 조회
     */
    @GetMapping("/hot")
    public ResponseEntity<HotPostDto.HotPostListResponse> getHotPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int period) {
        try {
            log.info("HOT 게시글 목록 조회 요청 - 페이지: {}, 크기: {}, 기간: {}일", page, size, period);
            
            HotPostDto.HotPostListResponse response = hotPostService.getHotPosts(page, size, period);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("HOT 게시글 목록 조회 오류: ", e);
            return ResponseEntity.badRequest().build();
        }
    }
} 