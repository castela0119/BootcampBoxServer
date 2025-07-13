package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.TagDto;
import com.bootcampbox.server.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Slf4j
public class TagController {

    private final TagService tagService;

    // 태그 생성 (관리자용)
    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody TagDto.CreateTagRequest request) {
        try {
            log.info("태그 생성 요청: {}", request.getName());
            TagDto.TagResponse response = tagService.createTag(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("태그 생성 오류: ", e);
            return ResponseEntity.badRequest().body("태그 생성 실패: " + e.getMessage());
        }
    }

    // 태그 수정 (관리자용)
    @PutMapping("/{tagId}")
    public ResponseEntity<?> updateTag(@PathVariable Long tagId, @RequestBody TagDto.UpdateTagRequest request) {
        try {
            log.info("태그 수정 요청: {}", tagId);
            TagDto.TagResponse response = tagService.updateTag(tagId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("태그 수정 오류: ", e);
            return ResponseEntity.badRequest().body("태그 수정 실패: " + e.getMessage());
        }
    }

    // 태그 삭제 (관리자용)
    @DeleteMapping("/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId) {
        try {
            log.info("태그 삭제 요청: {}", tagId);
            tagService.deleteTag(tagId);
            return ResponseEntity.ok("태그 삭제 완료");
        } catch (Exception e) {
            log.error("태그 삭제 오류: ", e);
            return ResponseEntity.badRequest().body("태그 삭제 실패: " + e.getMessage());
        }
    }

    // 전체 태그 목록 조회
    @GetMapping
    public ResponseEntity<?> getAllTags() {
        try {
            log.info("전체 태그 목록 조회");
            TagDto.TagListResponse response = tagService.getAllTags();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("태그 목록 조회 오류: ", e);
            return ResponseEntity.badRequest().body("태그 목록 조회 실패: " + e.getMessage());
        }
    }

    // 태그 타입별 조회
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getTagsByType(@PathVariable String type) {
        try {
            log.info("태그 타입별 조회: {}", type);
            TagDto.TagListResponse response = tagService.getTagsByType(type);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("태그 타입별 조회 오류: ", e);
            return ResponseEntity.badRequest().body("태그 타입별 조회 실패: " + e.getMessage());
        }
    }

    // 태그 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchTags(@RequestParam String keyword, 
                                       @RequestParam(required = false) String type,
                                       @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("태그 검색: {}", keyword);
            TagDto.TagSearchRequest request = new TagDto.TagSearchRequest(keyword, type, limit);
            TagDto.TagListResponse response = tagService.searchTags(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("태그 검색 오류: ", e);
            return ResponseEntity.badRequest().body("태그 검색 실패: " + e.getMessage());
        }
    }

    // 인기 태그 조회
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("인기 태그 조회 (상위 {}개)", limit);
            TagDto.TagListResponse response = tagService.getPopularTags(limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("인기 태그 조회 오류: ", e);
            return ResponseEntity.badRequest().body("인기 태그 조회 실패: " + e.getMessage());
        }
    }

    // 게시글의 태그 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getTagsByPostId(@PathVariable Long postId) {
        try {
            log.info("게시글 태그 조회: {}", postId);
            TagDto.TagListResponse response = tagService.getTagsByPostId(postId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 태그 조회 오류: ", e);
            return ResponseEntity.badRequest().body("게시글 태그 조회 실패: " + e.getMessage());
        }
    }
} 