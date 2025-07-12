package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.*;
import com.bootcampbox.server.dto.PostActionDto;
import com.bootcampbox.server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostActionService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostReportRepository postReportRepository;
    private final BookmarkRepository bookmarkRepository;

    // === 게시글 좋아요 ===
    public PostActionDto.ActionResponse likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀는지 확인
        if (postLikeRepository.findByPostIdAndUserId(postId, user.getId()).isPresent()) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }

        // 좋아요 생성
        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);
        postLikeRepository.save(postLike);

        // 게시글 좋아요 수 증가
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);

        log.info("게시글 좋아요 완료 - 게시글: {}, 사용자: {}", postId, username);
        
        return new PostActionDto.ActionResponse(
                "게시글 좋아요 완료", 
                post.getLikeCount(), 
                true, 
                isReported(postId, user.getId()), 
                isBookmarked(postId, user.getId())
        );
    }

    public PostActionDto.ActionResponse unlikePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 좋아요 삭제
        PostLike postLike = postLikeRepository.findByPostIdAndUserId(postId, user.getId())
                .orElseThrow(() -> new RuntimeException("좋아요를 찾을 수 없습니다."));
        
        postLikeRepository.delete(postLike);

        // 게시글 좋아요 수 감소
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);

        log.info("게시글 좋아요 취소 완료 - 게시글: {}, 사용자: {}", postId, username);
        
        return new PostActionDto.ActionResponse(
                "게시글 좋아요 취소 완료", 
                post.getLikeCount(), 
                false, 
                isReported(postId, user.getId()), 
                isBookmarked(postId, user.getId())
        );
    }

    // === 게시글 신고 ===
    public PostActionDto.ActionResponse reportPost(Long postId, String username, PostActionDto.ReportRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 이미 신고했는지 확인
        if (postReportRepository.findByPostIdAndUserId(postId, user.getId()).isPresent()) {
            throw new RuntimeException("이미 신고했습니다.");
        }

        // 신고 생성
        PostReport postReport = new PostReport();
        postReport.setPost(post);
        postReport.setUser(user);
        postReport.setReason(request.getReason());
        postReportRepository.save(postReport);

        // 게시글 신고 수 증가
        post.setReportCount(post.getReportCount() + 1);
        postRepository.save(post);

        log.info("게시글 신고 완료 - 게시글: {}, 사용자: {}, 사유: {}", postId, username, request.getReason());
        
        return new PostActionDto.ActionResponse(
                "게시글 신고 완료", 
                post.getReportCount(), 
                isLiked(postId, user.getId()), 
                true, 
                isBookmarked(postId, user.getId())
        );
    }

    // === 게시글 북마크 ===
    public PostActionDto.ActionResponse bookmarkPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 이미 북마크했는지 확인
        if (bookmarkRepository.findByPostIdAndUserId(postId, user.getId()).isPresent()) {
            throw new RuntimeException("이미 북마크했습니다.");
        }

        // 북마크 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setPost(post);
        bookmark.setUser(user);
        bookmarkRepository.save(bookmark);

        log.info("게시글 북마크 완료 - 게시글: {}, 사용자: {}", postId, username);
        
        return new PostActionDto.ActionResponse(
                "게시글 북마크 완료", 
                0, 
                isLiked(postId, user.getId()), 
                isReported(postId, user.getId()), 
                true
        );
    }

    public PostActionDto.ActionResponse unbookmarkPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 북마크 삭제
        Bookmark bookmark = bookmarkRepository.findByPostIdAndUserId(postId, user.getId())
                .orElseThrow(() -> new RuntimeException("북마크를 찾을 수 없습니다."));
        
        bookmarkRepository.delete(bookmark);

        log.info("게시글 북마크 취소 완료 - 게시글: {}, 사용자: {}", postId, username);
        
        return new PostActionDto.ActionResponse(
                "게시글 북마크 취소 완료", 
                0, 
                isLiked(postId, user.getId()), 
                isReported(postId, user.getId()), 
                false
        );
    }

    // === 관리자용 메서드 ===
    public PostActionDto.UserListResponse getPostLikes(Long postId) {
        List<Long> userIds = postLikeRepository.findUserIdsByPostId(postId);
        return new PostActionDto.UserListResponse(userIds, userIds.size());
    }

    public PostActionDto.UserListResponse getPostReports(Long postId) {
        List<Long> userIds = postReportRepository.findUserIdsByPostId(postId);
        return new PostActionDto.UserListResponse(userIds, userIds.size());
    }

    public PostActionDto.ActionResponse unreportPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 신고 삭제
        PostReport postReport = postReportRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다."));
        
        postReportRepository.delete(postReport);

        // 게시글 신고 수 감소
        post.setReportCount(Math.max(0, post.getReportCount() - 1));
        postRepository.save(post);

        log.info("게시글 신고 취소 완료 - 게시글: {}, 사용자: {}", postId, userId);
        
        return new PostActionDto.ActionResponse("게시글 신고 취소 완료", post.getReportCount(), false, false, false);
    }

    // === 내가 좋아요/북마크한 게시글 목록 ===
    public List<Long> getMyLikedPosts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return postLikeRepository.findPostIdsByUserId(user.getId());
    }

    public List<Long> getMyBookmarkedPosts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return bookmarkRepository.findPostIdsByUserId(user.getId());
    }

    // === 상태 확인 메서드 ===
    private boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    private boolean isReported(Long postId, Long userId) {
        return postReportRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    private boolean isBookmarked(Long postId, Long userId) {
        return bookmarkRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }
} 