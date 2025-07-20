package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.PostMute;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.PostMuteDto;
import com.bootcampbox.server.repository.PostMuteRepository;
import com.bootcampbox.server.repository.PostRepository;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostMuteService {
    
    private final PostMuteRepository postMuteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    // 게시글 뮤트 상태 확인
    public boolean isPostMuted(Long userId, Long postId) {
        return postMuteRepository.existsByUserIdAndPostId(userId, postId);
    }
    
    // 게시글 뮤트
    @Transactional
    public PostMuteDto.MuteResponse mutePost(Long userId, Long postId) {
        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));
        
        // 이미 뮤트된 경우
        if (isPostMuted(userId, postId)) {
            throw new IllegalStateException("이미 뮤트된 게시글입니다: " + postId);
        }
        
        // 뮤트 생성
        PostMute postMute = new PostMute(userId, postId);
        postMuteRepository.save(postMute);
        
        log.info("게시글 뮤트 - 사용자: {}, 게시글: {}", userId, postId);
        
        return PostMuteDto.MuteResponse.from(postMute);
    }
    
    // 게시글 언뮤트
    @Transactional
    public PostMuteDto.UnmuteResponse unmutePost(Long userId, Long postId) {
        // 게시글 존재 확인
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId);
        }
        
        // 뮤트 삭제
        postMuteRepository.deleteByUserIdAndPostId(userId, postId);
        
        log.info("게시글 언뮤트 - 사용자: {}, 게시글: {}", userId, postId);
        
        return new PostMuteDto.UnmuteResponse(postId, userId);
    }
    
    // 게시글 뮤트 상태 조회
    public PostMuteDto.MuteStatusResponse getMuteStatus(Long userId, Long postId) {
        // 게시글 존재 확인
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId);
        }
        
        // 뮤트 상태 확인
        if (isPostMuted(userId, postId)) {
            PostMute postMute = postMuteRepository.findByUserIdAndPostId(userId, postId)
                    .orElseThrow(() -> new RuntimeException("뮤트 정보를 찾을 수 없습니다."));
            return PostMuteDto.MuteStatusResponse.muted(postMute.getCreatedAt());
        } else {
            return PostMuteDto.MuteStatusResponse.notMuted();
        }
    }
    
    // 사용자별 뮤트된 게시글 목록 조회
    public PostMuteDto.MutedPostsResponse getUserMutedPosts(Long userId, int page, int size) {
        // 페이징 설정
        Pageable pageable = PageRequest.of(page, Math.min(size, 50)); // 최대 50개로 제한
        
        // 뮤트된 게시글 조회
        Page<PostMute> mutedPostsPage = postMuteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        // 게시글 정보와 함께 변환
        List<PostMuteDto.MutedPostInfo> mutedPostsInfo = mutedPostsPage.getContent().stream()
                .map(postMute -> {
                    Post post = postRepository.findById(postMute.getPostId()).orElse(null);
                    if (post != null) {
                        User author = userRepository.findById(post.getUser().getId()).orElse(null);
                        String authorName = author != null ? author.getNickname() : "알 수 없음";
                        return PostMuteDto.MutedPostInfo.from(postMute, post.getTitle(), authorName);
                    } else {
                        // 게시글이 삭제된 경우
                        return PostMuteDto.MutedPostInfo.from(postMute, "[삭제된 게시글]", "알 수 없음");
                    }
                })
                .collect(Collectors.toList());
        
        // 페이징 정보 생성
        PostMuteDto.PaginationInfo pagination = PostMuteDto.PaginationInfo.from(
                page,
                mutedPostsPage.getTotalPages(),
                mutedPostsPage.getTotalElements(),
                mutedPostsPage.hasNext()
        );
        
        return PostMuteDto.MutedPostsResponse.of(mutedPostsInfo, pagination);
    }
    
    // 뮤트된 게시글 수 조회
    public long getMutedPostsCount(Long userId) {
        return postMuteRepository.countByUserId(userId);
    }
    
    // 게시글 뮤트 정보 조회
    public PostMute getMuteInfo(Long userId, Long postId) {
        return postMuteRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new RuntimeException("뮤트 정보를 찾을 수 없습니다."));
    }
} 