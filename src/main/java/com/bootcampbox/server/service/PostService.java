package com.bootcampbox.server.service;

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

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TagService tagService;

    public PostDto.Response createPost(String username, PostDto.CreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = new Post();
        post.setUser(user);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        
        // 작성 당시 사용자의 신분 type 저장 (Request에서 받은 값 우선, 없으면 사용자 정보에서)
        String authorUserType = (request.getAuthorUserType() != null && !request.getAuthorUserType().trim().isEmpty()) 
            ? request.getAuthorUserType() 
            : user.getUserType();
        post.setAuthorUserType(authorUserType);
        
        // 익명 여부는 사용자가 선택
        post.setAnonymous(request.isAnonymous());
        if (request.isAnonymous()) {
            post.setAnonymousNickname("익명");
        }

        // 태그 처리
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            List<Tag> tags = tagService.getTagsByNames(request.getTagNames());
            for (Tag tag : tags) {
                post.addTag(tag);
            }
        }

        Post savedPost = postRepository.save(post);
        return PostDto.Response.from(savedPost);
    }

    public PostDto.Response getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return PostDto.Response.from(post);
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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getUsername().equals(username)) {
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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getUsername().equals(username)) {
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
            cond.setSort(sortBy.trim());
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
} 