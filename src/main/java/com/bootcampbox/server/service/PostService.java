package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Category;
import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.Tag;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.PostDto;
import com.bootcampbox.server.repository.PostRepository;
import com.bootcampbox.server.repository.UserRepository;
import com.bootcampbox.server.repository.PostLikeRepository;
import com.bootcampbox.server.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.bootcampbox.server.dto.PostSearchCondition;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TagService tagService;
    private final HotPostService hotPostService;
    private final CategoryService categoryService;

    public PostDto.Response createPost(String username, PostDto.CreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return createPost(user, request);
    }

    public PostDto.Response createPost(User user, PostDto.CreateRequest request) {
        // User 객체를 다시 조회하여 persistent 상태로 만듦
        User persistentUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Post post = new Post();
        post.setUser(persistentUser);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        
        // 작성 당시 사용자의 신분 type 저장 (Request에서 받은 값 우선, 없으면 사용자 정보에서)
        String authorUserType = (request.getAuthorUserType() != null && !request.getAuthorUserType().trim().isEmpty()) 
            ? request.getAuthorUserType() 
            : persistentUser.getUserType();
        post.setAuthorUserType(authorUserType);
        
        // 익명 여부는 사용자가 선택
        post.setAnonymous(request.isAnonymous());
        if (request.isAnonymous()) {
            post.setAnonymousNickname("익명");
        }

        // 카테고리 설정 (category 필드 우선, 없으면 categoryId 사용)
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            // category 문자열을 DB 값으로 매핑
            String dbCategory = mapCategoryToDbValue(request.getCategory());
            Category category = categoryService.getCategoryByEnglishName(dbCategory);
            post.setCategory(category);
        } else if (request.getCategoryId() != null) {
            Category category = categoryService.getCategoryEntity(request.getCategoryId());
            post.setCategory(category);
        } else {
            // 카테고리가 지정되지 않은 경우 기본 카테고리(커뮤니티 탭 게시판)로 설정
            Category defaultCategory = categoryService.getCategoryByName("커뮤니티 탭 게시판");
            post.setCategory(defaultCategory);
        }

        // 태그 처리
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            List<Tag> tags = tagService.getTagsByNames(request.getTagNames());
            for (Tag tag : tags) {
                post.addTag(tag);
            }
        }

        Post savedPost = postRepository.save(post);
        
        // HOT 점수 초기화
        hotPostService.initializePostHotScore(savedPost);
        
        return PostDto.Response.from(savedPost);
    }

    public PostDto.Response getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return PostDto.Response.from(post);
    }

    // getPostById 메서드 추가 (NoticeController에서 사용)
    public PostDto.Response getPostById(Long postId) {
        return getPost(postId);
    }

    public PostDto.Response getPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        // 현재 사용자 정보 가져오기
        User currentUser = userRepository.findByUsername(username)
                .orElse(null);
        
        boolean isLiked = false;
        boolean isBookmarked = false;
        
        if (currentUser != null) {
            // 좋아요 상태 확인
            isLiked = postLikeRepository.findByPostIdAndUserId(postId, currentUser.getId()).isPresent();
            // 북마크 상태 확인
            isBookmarked = bookmarkRepository.findByPostIdAndUserId(postId, currentUser.getId()).isPresent();
        }
        
        return PostDto.Response.from(post, currentUser != null ? currentUser.getId() : null, isLiked, isBookmarked);
    }

    // incrementViewCount 메서드 추가 (NoticeController에서 사용)
    public void incrementViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    // 기존 메서드는 새로운 필터링 메서드로 대체
    public Page<PostDto.Response> getAllPosts(Pageable pageable) {
        return getAllPostsWithFilters(pageable.getPageNumber(), pageable.getPageSize(), null, null, null, null, null);
    }

    public Page<PostDto.Response> getPostsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return postRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(PostDto.Response::from);
    }

    public PostDto.Response updatePost(Long postId, String username, PostDto.UpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return updatePost(user, postId, request);
    }

    public PostDto.Response updatePost(User user, Long postId, PostDto.UpdateRequest request) {
        // User 객체를 다시 조회하여 persistent 상태로 만듦
        User persistentUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getUsername().equals(persistentUser.getUsername())) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
        }

        // 익명 게시글은 수정 불가
        if (post.isAnonymous()) {
            throw new IllegalArgumentException("익명으로 작성된 게시글은 수정할 수 없습니다.");
        }
        
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        
        // 익명 여부는 수정 불가 (기존 설정 유지)

        // 태그 처리 (기존 태그 제거 후 새로운 태그 추가)
        post.getTags().clear();
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            List<Tag> tags = tagService.getTagsByNames(request.getTagNames());
            for (Tag tag : tags) {
                post.addTag(tag);
            }
        }

        Post updatedPost = postRepository.save(post);
        return PostDto.Response.from(updatedPost);
    }

    public void deletePost(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        deletePost(user, postId);
    }

    public void deletePost(User user, Long postId) {
        // User 객체를 다시 조회하여 persistent 상태로 만듦
        User persistentUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getUsername().equals(persistentUser.getUsername())) {
            throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다.");
        }

        // 익명 게시글도 삭제는 가능
        postRepository.delete(post);
    }

    // Post ID로 Post 엔티티 조회 (내부용)
    public Post getPostEntity(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));
    }

    // 태그 검색 관련 메서드들
    public Page<PostDto.Response> getPostsByTag(String tagName, Pageable pageable) {
        return postRepository.findByTagName(tagName, pageable)
                .map(PostDto.Response::from);
    }

    public Page<PostDto.Response> getPostsByTags(List<String> tagNames, String operator, Pageable pageable) {
        if ("AND".equalsIgnoreCase(operator)) {
            // 모든 태그가 포함된 게시글 (AND)
            return postRepository.findByAllTags(tagNames, (long) tagNames.size(), pageable)
                    .map(PostDto.Response::from);
        } else {
            // 하나라도 포함된 게시글 (OR)
            return postRepository.findByAnyTags(tagNames, pageable)
                    .map(PostDto.Response::from);
        }
    }

    public Page<PostDto.Response> searchPostsByKeyword(String keyword, Pageable pageable) {
        return postRepository.searchByKeyword(keyword, pageable)
                .map(PostDto.Response::from);
    }

    public Page<PostDto.Response> getAllPostsWithFilters(
            int page, int size, String search, String authorUserType, 
            String tags, String sortBy, String sortOrder) {
        Pageable pageable = createPageable(page, size, sortBy, sortOrder);

        // 검색 조건 객체 생성
        PostSearchCondition cond = new PostSearchCondition();
        if (search != null && !search.trim().isEmpty()) {
            String trimmedSearch = search.trim();
            if (trimmedSearch.length() >= 2) {
                cond.setKeyword(trimmedSearch);
            } else {
                throw new IllegalArgumentException("검색어는 2글자 이상 입력해주세요.");
            }
        }
        if (authorUserType != null && !authorUserType.trim().isEmpty()) {
            cond.setUserType(authorUserType.trim());
        }
        if (tags != null && !tags.trim().isEmpty()) {
            List<String> tagList = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toList());
            cond.setTagList(tagList);
        }
                    if (sortBy != null && !sortBy.trim().isEmpty()) {
                cond.setSortBy(sortBy.trim());
            }

        Page<Post> postPage = postRepository.searchPosts(cond, pageable);
        return postPage.map(PostDto.Response::from);
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // 기본값: 최신순
        
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            
            switch (sortBy.toLowerCase()) {
                case "likes":
                    sort = Sort.by(direction, "likeCount");
                    break;
                case "views":
                    sort = Sort.by(direction, "viewCount");
                    break;
                case "comments":
                    sort = Sort.by(direction, "commentCount");
                    break;
                case "created":
                default:
                    sort = Sort.by(direction, "createdAt");
                    break;
            }
        }
        
        return PageRequest.of(page, size, sort);
    }

    // ===== 카테고리별 게시글 조회 메서드 =====

    // 카테고리명으로 게시글 조회
    public Page<PostDto.Response> getPostsByCategory(String categoryName, int page, int size, String sortBy, String sortOrder) {
        log.info("=== 카테고리별 게시글 조회 시작 ===");
        log.info("카테고리명: {}", categoryName);
        log.info("페이지: {}, 크기: {}, 정렬: {}, 순서: {}", page, size, sortBy, sortOrder);
        
        Pageable pageable = createPageable(page, size, sortBy, sortOrder);
        log.info("Pageable 생성 완료: {}", pageable);
        
        Page<Post> posts = postRepository.findByCategoryName(categoryName, pageable);
        log.info("DB 조회 완료: 총 {}개 게시글, 현재 페이지 {}개", posts.getTotalElements(), posts.getNumberOfElements());
        
        // 조회된 게시글들의 카테고리 정보 로그
        posts.getContent().forEach(post -> {
            log.info("게시글 ID: {}, 제목: {}, 카테고리: {}", 
                post.getId(), post.getTitle(), 
                post.getCategory() != null ? post.getCategory().getName() : "NULL");
        });
        
        Page<PostDto.Response> result = posts.map(post -> PostDto.Response.from(post));
        log.info("=== 카테고리별 게시글 조회 완료 ===");
        return result;
    }

    // 카테고리 ID로 게시글 조회
    public Page<PostDto.Response> getPostsByCategoryId(Long categoryId, int page, int size, String sortBy, String sortOrder) {
        log.info("=== 카테고리 ID별 게시글 조회 시작 ===");
        log.info("카테고리 ID: {}", categoryId);
        log.info("페이지: {}, 크기: {}, 정렬: {}, 순서: {}", page, size, sortBy, sortOrder);
        
        Pageable pageable = createPageable(page, size, sortBy, sortOrder);
        log.info("Pageable 생성 완료: {}", pageable);
        
        Page<Post> posts = postRepository.findByCategoryId(categoryId, pageable);
        log.info("DB 조회 완료: 총 {}개 게시글, 현재 페이지 {}개", posts.getTotalElements(), posts.getNumberOfElements());
        
        // 조회된 게시글들의 카테고리 정보 로그
        posts.getContent().forEach(post -> {
            log.info("게시글 ID: {}, 제목: {}, 카테고리: {}", 
                post.getId(), post.getTitle(), 
                post.getCategory() != null ? post.getCategory().getName() : "NULL");
        });
        
        Page<PostDto.Response> result = posts.map(post -> PostDto.Response.from(post));
        log.info("=== 카테고리 ID별 게시글 조회 완료 ===");
        return result;
    }

    // 영문 카테고리명으로 게시글 조회
    public Page<PostDto.Response> getPostsByEnglishCategoryName(String englishName, int page, int size, String sortBy, String sortOrder) {
        log.info("=== 영문 카테고리별 게시글 조회 시작 ===");
        log.info("영문 카테고리명: {}", englishName);
        log.info("페이지: {}, 크기: {}, 정렬: {}, 순서: {}", page, size, sortBy, sortOrder);
        
        Pageable pageable = createPageable(page, size, sortBy, sortOrder);
        log.info("Pageable 생성 완료: {}", pageable);
        
        Page<Post> posts = postRepository.findByEnglishCategoryName(englishName, pageable);
        log.info("DB 조회 완료: 총 {}개 게시글, 현재 페이지 {}개", posts.getTotalElements(), posts.getNumberOfElements());
        
        // 조회된 게시글들의 카테고리 정보 로그
        posts.getContent().forEach(post -> {
            log.info("게시글 ID: {}, 제목: {}, 카테고리: {}", 
                post.getId(), post.getTitle(), 
                post.getCategory() != null ? post.getCategory().getName() : "NULL");
        });
        
        Page<PostDto.Response> result = posts.map(post -> PostDto.Response.from(post));
        log.info("=== 영문 카테고리별 게시글 조회 완료 ===");
        return result;
    }

    /**
     * 카테고리 매핑 (요구사항 -> 실제 DB 값)
     */
    private String mapCategoryToDbValue(String category) {
        if (category == null) {
            throw new IllegalArgumentException("카테고리가 null입니다.");
        }
        
        String normalizedCategory = category.trim();
        
        // 영문 카테고리명 매핑
        switch (normalizedCategory.toUpperCase()) {
            case "CAREER_COUNSEL":
                return "career";
            case "LOVE_COUNSEL":
                return "love";
            case "INCIDENT":
                return "incident";
            case "VACATION":
                return "vacation";
            case "COMMUNITY_BOARD":
                return "community";
            case "NOTICE":
                return "notice";
        }
        
        // 한글 카테고리명 매핑
        switch (normalizedCategory) {
            case "진로 상담":
                return "career";
            case "연애 상담":
                return "love";
            case "사건 사고":
                return "incident";
            case "휴가 어때":
                return "vacation";
            case "커뮤니티 탭 게시판":
            case "커뮤니티":
                return "community";
            case "공지사항":
                return "notice";
        }
        
        throw new IllegalArgumentException("지원하지 않는 카테고리입니다: " + category);
    }

    // searchPostsByCategory 메서드 추가 (NoticeController에서 사용)
    public PostDto.CategorySearchResponse searchPostsByCategory(
            String category, String search, String authorUserType, 
            List<String> tags, String sortBy, String sortOrder, int page, int size) {
        
        // 카테고리 매핑
        String dbCategory = mapCategoryToDbValue(category);
        
        // 검색 조건 검증
        if (search != null && !search.trim().isEmpty()) {
            String trimmedSearch = search.trim();
            if (trimmedSearch.length() < 2) {
                throw new IllegalArgumentException("검색어는 2글자 이상 입력해주세요.");
            }
        }
        
        // 정렬 조건 검증
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            if (!isValidSortBy(sortBy.trim())) {
                throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다: " + sortBy);
            }
        }
        
        if (sortOrder != null && !sortOrder.trim().isEmpty()) {
            if (!isValidSortOrder(sortOrder.trim())) {
                throw new IllegalArgumentException("지원하지 않는 정렬 순서입니다: " + sortOrder);
            }
        }
        
        Pageable pageable = createPageable(page, size, sortBy, sortOrder);
        Page<Post> postPage;
        
        // 검색 조건에 따른 쿼리 분기
        if (search != null && !search.trim().isEmpty()) {
            if (authorUserType != null && !authorUserType.trim().isEmpty()) {
                if (tags != null && !tags.isEmpty()) {
                    postPage = postRepository.findByCategoryAndSearchKeywordAndAuthorUserTypeAndTags(
                            dbCategory, search.trim(), authorUserType.trim(), tags, pageable);
                } else {
                    postPage = postRepository.findByCategoryAndSearchKeywordAndAuthorUserType(
                            dbCategory, search.trim(), authorUserType.trim(), pageable);
                }
            } else {
                if (tags != null && !tags.isEmpty()) {
                    postPage = postRepository.findByCategoryAndSearchKeywordAndTags(
                            dbCategory, search.trim(), tags, pageable);
                } else {
                    postPage = postRepository.findByCategoryAndSearchKeyword(
                            dbCategory, search.trim(), pageable);
                }
            }
        } else {
            if (authorUserType != null && !authorUserType.trim().isEmpty()) {
                if (tags != null && !tags.isEmpty()) {
                    postPage = postRepository.findByCategoryAndAuthorUserTypeAndTags(
                            dbCategory, authorUserType.trim(), tags, pageable);
                } else {
                    postPage = postRepository.findByCategoryAndAuthorUserType(
                            dbCategory, authorUserType.trim(), pageable);
                }
            } else {
                if (tags != null && !tags.isEmpty()) {
                    postPage = postRepository.findByCategoryAndTags(dbCategory, tags, pageable);
                } else {
                    postPage = postRepository.findByCategory(dbCategory, pageable);
                }
            }
        }
        
        // 응답 생성
        List<PostDto.Response> posts = postPage.getContent().stream()
                .map(PostDto.Response::from)
                .collect(Collectors.toList());
        
        PostDto.CategorySearchResponse.PaginationInfo pagination = PostDto.CategorySearchResponse.PaginationInfo.builder()
                .currentPage(postPage.getNumber())
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .size(postPage.getSize())
                .hasNext(postPage.hasNext())
                .hasPrevious(postPage.hasPrevious())
                .build();
        
        PostDto.CategorySearchResponse.SearchInfo searchInfo = PostDto.CategorySearchResponse.SearchInfo.builder()
                .searchKeyword(search)
                .category(category)
                .resultCount((int) postPage.getTotalElements())
                .build();
        
        return PostDto.CategorySearchResponse.builder()
                .posts(posts)
                .pagination(pagination)
                .searchInfo(searchInfo)
                .build();
    }

    // 정렬 기준 유효성 검사 메서드
    private boolean isValidSortBy(String sortBy) {
        return sortBy.equals("createdAt") || 
               sortBy.equals("likeCount") || 
               sortBy.equals("viewCount") || 
               sortBy.equals("commentCount");
    }

    // 정렬 순서 유효성 검사 메서드
    private boolean isValidSortOrder(String sortOrder) {
        return sortOrder.equals("asc") || sortOrder.equals("desc");
    }
} 