package com.bootcampbox.server.controller;

import com.bootcampbox.server.config.CurrentUser;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.ApiResponse;
import com.bootcampbox.server.dto.PostDto;
import com.bootcampbox.server.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final PostService postService;

    /**
     * 공지사항 목록 조회 (모든 사용자 접근 가능)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PostDto.CategorySearchResponse>> getNotices(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("=== 공지사항 목록 조회 요청 시작 ===");
        log.info("검색어: {}, 정렬: {}, 순서: {}, 페이지: {}, 크기: {}", 
                search, sortBy, sortOrder, page, size);

        try {
            PostDto.CategorySearchResponse response = postService.searchPostsByCategory(
                    "NOTICE", search, null, null, sortBy, sortOrder, page, size);

            log.info("=== 공지사항 목록 조회 요청 완료 ===");
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.error("공지사항 목록 조회 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("공지사항 목록 조회 중 예상치 못한 오류: ", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }

    /**
     * 공지사항 상세 조회 (모든 사용자 접근 가능)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDto.Response>> getNotice(@PathVariable Long id) {
        log.info("=== 공지사항 상세 조회 요청 시작 ===");
        log.info("공지사항 ID: {}", id);

        try {
            PostDto.Response response = postService.getPostById(id);
            
            // 공지사항 카테고리인지 확인
            if (!"notice".equals(response.getCategory().getEnglishName())) {
                log.error("요청된 게시글이 공지사항이 아닙니다. ID: {}", id);
                return ResponseEntity.badRequest().body(ApiResponse.error("공지사항이 아닙니다."));
            }

            log.info("=== 공지사항 상세 조회 요청 완료 ===");
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.error("공지사항 상세 조회 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("공지사항 상세 조회 중 예상치 못한 오류: ", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }

    /**
     * 공지사항 작성 (관리자만 접근 가능)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PostDto.Response>> createNotice(
            @CurrentUser User currentUser,
            @Valid @RequestBody PostDto.CreateRequest request) {

        log.info("=== 공지사항 작성 요청 시작 ===");
        log.info("작성자: {}, 제목: {}", currentUser.getNickname(), request.getTitle());

        try {
            // 공지사항 카테고리로 강제 설정
            request.setCategory("NOTICE");
            
            PostDto.Response response = postService.createPost(currentUser, request);

            log.info("=== 공지사항 작성 요청 완료 ===");
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.error("공지사항 작성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("공지사항 작성 중 예상치 못한 오류: ", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }

    /**
     * 공지사항 수정 (관리자만 접근 가능)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PostDto.Response>> updateNotice(
            @CurrentUser User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody PostDto.UpdateRequest request) {

        log.info("=== 공지사항 수정 요청 시작 ===");
        log.info("수정자: {}, 공지사항 ID: {}", currentUser.getNickname(), id);

        try {
            PostDto.Response response = postService.updatePost(currentUser, id, request);

            // 공지사항 카테고리인지 확인
            if (!"notice".equals(response.getCategory().getEnglishName())) {
                log.error("요청된 게시글이 공지사항이 아닙니다. ID: {}", id);
                return ResponseEntity.badRequest().body(ApiResponse.error("공지사항이 아닙니다."));
            }

            log.info("=== 공지사항 수정 요청 완료 ===");
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.error("공지사항 수정 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("공지사항 수정 중 예상치 못한 오류: ", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }

    /**
     * 공지사항 삭제 (관리자만 접근 가능)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @CurrentUser User currentUser,
            @PathVariable Long id) {

        log.info("=== 공지사항 삭제 요청 시작 ===");
        log.info("삭제자: {}, 공지사항 ID: {}", currentUser.getNickname(), id);

        try {
            // 공지사항인지 먼저 확인
            PostDto.Response post = postService.getPostById(id);
            if (!"notice".equals(post.getCategory().getEnglishName())) {
                log.error("요청된 게시글이 공지사항이 아닙니다. ID: {}", id);
                return ResponseEntity.badRequest().body(ApiResponse.error("공지사항이 아닙니다."));
            }

            postService.deletePost(currentUser, id);

            log.info("=== 공지사항 삭제 요청 완료 ===");
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            log.error("공지사항 삭제 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("공지사항 삭제 중 예상치 못한 오류: ", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }

    /**
     * 공지사항 조회수 증가
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable Long id) {
        log.info("=== 공지사항 조회수 증가 요청 시작 ===");
        log.info("공지사항 ID: {}", id);

        try {
            // 공지사항인지 먼저 확인
            PostDto.Response post = postService.getPostById(id);
            if (!"notice".equals(post.getCategory().getEnglishName())) {
                log.error("요청된 게시글이 공지사항이 아닙니다. ID: {}", id);
                return ResponseEntity.badRequest().body(ApiResponse.error("공지사항이 아닙니다."));
            }

            postService.incrementViewCount(id);

            log.info("=== 공지사항 조회수 증가 요청 완료 ===");
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            log.error("공지사항 조회수 증가 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("공지사항 조회수 증가 중 예상치 못한 오류: ", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }
} 