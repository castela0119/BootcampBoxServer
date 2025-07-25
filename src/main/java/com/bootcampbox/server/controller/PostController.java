package com.bootcampbox.server.controller;

import com.bootcampbox.server.config.CurrentUser;
import com.bootcampbox.server.dto.ApiResponse;
import com.bootcampbox.server.dto.PostDto;
import com.bootcampbox.server.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.repository.UserRepository;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<PostDto.Response>> createPost(
            @CurrentUser String username,
            @Valid @RequestBody PostDto.CreateRequest request) {
        log.info("게시글 작성 요청: username={}, title={}", username, request.getTitle());
        
        // 공지사항 카테고리일 때 관리자 권한 확인
        if (request.getCategory() != null && 
            (request.getCategory().equals("NOTICE") || request.getCategory().equals("공지사항"))) {
            // 현재 사용자가 관리자인지 확인
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            if (!"관리자".equals(currentUser.getUserType())) {
                log.error("공지사항 작성 권한 없음: username={}, userType={}", username, currentUser.getUserType());
                return ResponseEntity.status(403).body(ApiResponse.error("공지사항은 관리자만 작성할 수 있습니다."));
            }
        }
        
        PostDto.Response response = postService.createPost(username, request);
        return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 작성되었습니다.", response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDto.Response>> getPost(
            @PathVariable Long postId,
            @CurrentUser String username) {
        log.info("게시글 조회 요청: postId={}, username={}", postId, username);
        PostDto.Response response = postService.getPost(postId, username);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String authorUserType,
            @RequestParam(required = false) String tags, // 복수 태그 (콤마 구분)
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("=== 게시글 목록 조회 요청 시작 ===");
        log.info("카테고리: {}, 검색어: {}, 작성자신분: {}, 태그: {}, 정렬: {}, 순서: {}, 페이지: {}, 크기: {}", 
                category, search, authorUserType, tags, sortBy, sortOrder, page, size);
        
        try {
            // 태그 리스트 변환
            List<String> tagList = null;
            if (tags != null && !tags.trim().isEmpty()) {
                tagList = Arrays.stream(tags.split(","))
                        .map(String::trim)
                        .filter(tag -> !tag.isEmpty())
                        .collect(Collectors.toList());
            }
            
            // 카테고리가 지정된 경우 카테고리별 검색 실행
            if (category != null && !category.trim().isEmpty()) {
                PostDto.CategorySearchResponse response = postService.searchPostsByCategory(
                        category, search, authorUserType, tagList, sortBy, sortOrder, page, size);
                
                log.info("=== 카테고리별 게시글 목록 조회 요청 완료 ===");
                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                // 카테고리가 지정되지 않은 경우 기존 전체 검색 사용
                Page<PostDto.Response> response = postService.getAllPostsWithFilters(
                        page, size, search, authorUserType, tags, sortBy, sortOrder);
                
                log.info("=== 전체 게시글 목록 조회 요청 완료 ===");
                return ResponseEntity.ok(ApiResponse.success(response));
            }
            
        } catch (IllegalArgumentException e) {
            log.error("게시글 목록 조회 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 예상치 못한 오류: ", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostDto.Response>>> getPostsByUser(
            @PathVariable Long userId, 
            Pageable pageable) {
        log.info("사용자별 게시글 목록 조회 요청: userId={}, page={}, size={}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<PostDto.Response> response = postService.getPostsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDto.Response>> updatePost(
            @PathVariable Long postId,
            @CurrentUser String username,
            @Valid @RequestBody PostDto.UpdateRequest request) {
        log.info("게시글 수정 요청: postId={}, username={}, title={}", postId, username, request.getTitle());
        
        // 공지사항 카테고리일 때 관리자 권한 확인
        if (request.getCategory() != null && 
            (request.getCategory().equals("NOTICE") || request.getCategory().equals("공지사항"))) {
            // 현재 사용자가 관리자인지 확인
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            if (!"관리자".equals(currentUser.getUserType())) {
                log.error("공지사항 수정 권한 없음: username={}, userType={}", username, currentUser.getUserType());
                return ResponseEntity.status(403).body(ApiResponse.error("공지사항은 관리자만 수정할 수 있습니다."));
            }
        }
        
        PostDto.Response response = postService.updatePost(postId, username, request);
        return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 수정되었습니다.", response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @CurrentUser String username) {
        log.info("게시글 삭제 요청: postId={}, username={}", postId, username);
        
        // 공지사항인지 확인하고 관리자 권한 확인
        PostDto.Response post = postService.getPost(postId, username);
        if (post.getCategory() != null && "notice".equals(post.getCategory().getEnglishName())) {
            // 현재 사용자가 관리자인지 확인
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            if (!"관리자".equals(currentUser.getUserType())) {
                log.error("공지사항 삭제 권한 없음: username={}, userType={}", username, currentUser.getUserType());
                return ResponseEntity.status(403).body(ApiResponse.error("공지사항은 관리자만 삭제할 수 있습니다."));
            }
        }
        
        postService.deletePost(postId, username);
        return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 삭제되었습니다.", null));
    }

    // 태그 검색 관련 API들
    @GetMapping("/search/tag/{tagName}")
    public ResponseEntity<ApiResponse<Page<PostDto.Response>>> getPostsByTag(
            @PathVariable String tagName,
            Pageable pageable) {
        log.info("태그별 게시글 검색: tagName={}", tagName);
        Page<PostDto.Response> response = postService.getPostsByTag(tagName, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/tags")
    public ResponseEntity<ApiResponse<Page<PostDto.Response>>> getPostsByTags(
            @RequestParam List<String> tagNames,
            @RequestParam(defaultValue = "OR") String operator, // OR 또는 AND
            Pageable pageable) {
        log.info("다중 태그 게시글 검색: tagNames={}, operator={}", tagNames, operator);
        Page<PostDto.Response> response = postService.getPostsByTags(tagNames, operator, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<ApiResponse<Page<PostDto.Response>>> searchPostsByKeyword(
            @RequestParam String keyword,
            Pageable pageable) {
        log.info("키워드 게시글 검색: keyword={}", keyword);
        Page<PostDto.Response> response = postService.searchPostsByKeyword(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ===== 카테고리별 게시글 API =====

    // 커뮤니티 탭 게시판 (기존 게시글들이 여기로 분류됨)
    @GetMapping("/community")
    public ResponseEntity<Page<PostDto.Response>> getCommunityPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        log.info("커뮤니티 게시판 조회 요청: page={}, size={}, sortBy={}, sortOrder={}", 
                page, size, sortBy, sortOrder);
        Page<PostDto.Response> response = postService.getPostsByCategory("커뮤니티 탭 게시판", page, size, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    // 진로 상담 게시판
    @GetMapping("/career")
    public ResponseEntity<Page<PostDto.Response>> getCareerPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        log.info("진로 상담 게시판 조회 요청: page={}, size={}, sortBy={}, sortOrder={}", 
                page, size, sortBy, sortOrder);
        Page<PostDto.Response> response = postService.getPostsByCategory("진로 상담", page, size, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    // 연애 상담 게시판
    @GetMapping("/love")
    public ResponseEntity<Page<PostDto.Response>> getLovePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        log.info("연애 상담 게시판 조회 요청: page={}, size={}, sortBy={}, sortOrder={}", 
                page, size, sortBy, sortOrder);
        Page<PostDto.Response> response = postService.getPostsByCategory("연애 상담", page, size, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    // 사건 사고 게시판
    @GetMapping("/incident")
    public ResponseEntity<Page<PostDto.Response>> getIncidentPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        log.info("사건 사고 게시판 조회 요청: page={}, size={}, sortBy={}, sortOrder={}", 
                page, size, sortBy, sortOrder);
        Page<PostDto.Response> response = postService.getPostsByCategory("사건 사고", page, size, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    // 휴가 어때 게시판
    @GetMapping("/vacation")
    public ResponseEntity<Page<PostDto.Response>> getVacationPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        log.info("휴가 어때 게시판 조회 요청: page={}, size={}, sortBy={}, sortOrder={}", 
                page, size, sortBy, sortOrder);
        Page<PostDto.Response> response = postService.getPostsByCategory("휴가 어때", page, size, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    // 카테고리별 게시글 조회 (범용 API)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<PostDto.Response>>> getPostsByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        log.info("카테고리별 게시글 조회 요청: categoryId={}, page={}, size={}, sortBy={}, sortOrder={}", 
                categoryId, page, size, sortBy, sortOrder);
        Page<PostDto.Response> response = postService.getPostsByCategoryId(categoryId, page, size, sortBy, sortOrder);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 영문 카테고리명으로 게시글 조회 (범용 API)
    @GetMapping("/category/english/{englishName}")
    public ResponseEntity<ApiResponse<Page<PostDto.Response>>> getPostsByEnglishCategoryName(
            @PathVariable String englishName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        log.info("영문 카테고리별 게시글 조회 요청: englishName={}, page={}, size={}, sortBy={}, sortOrder={}", 
                englishName, page, size, sortBy, sortOrder);
        Page<PostDto.Response> response = postService.getPostsByEnglishCategoryName(englishName, page, size, sortBy, sortOrder);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 