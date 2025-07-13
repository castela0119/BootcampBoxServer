package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.Tag;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.PostDto;
import com.bootcampbox.server.repository.PostRepository;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    public PostDto.Response createPost(Long userId, PostDto.CreateRequest request) {
        User user = userRepository.findById(userId)
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

    public Page<PostDto.Response> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(PostDto.Response::from);
    }

    public Page<PostDto.Response> getPostsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return postRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(PostDto.Response::from);
    }

    public PostDto.Response updatePost(Long postId, Long userId, PostDto.UpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
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

    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
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
} 