package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Comment;
import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.CommentDto;
import com.bootcampbox.server.dto.CommentActionDto;
import com.bootcampbox.server.repository.CommentRepository;
import com.bootcampbox.server.repository.PostRepository;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentDto.CommentResponse createComment(Long postId, CommentDto.CreateCommentRequest request, String username) {
        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 사용자 존재 확인
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setUser(user);

        // 대댓글인 경우 부모 댓글 설정
        if (request.getParentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
            
            // 부모 댓글이 같은 게시글의 댓글인지 확인
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new IllegalArgumentException("부모 댓글이 해당 게시글의 댓글이 아닙니다.");
            }
            
            comment.setParent(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        return CommentDto.CommentResponse.from(savedComment, user.getId());
    }

    public CommentDto.CommentListResponse getComments(Long postId, int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);
        
        List<Comment> comments = commentPage.getContent();
        
        // 현재 사용자 ID 가져오기
        final Long currentUserId = userRepository.findByUsername(username)
                .map(User::getId)
                .orElse(null);
        
        // 각 댓글에 대댓글 추가
        List<CommentDto.CommentResponse> commentResponses = comments.stream()
            .map(comment -> {
                List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
                
                // 댓글의 좋아요 상태 확인
                boolean isLiked = false;
                if (currentUserId != null) {
                    isLiked = comment.getLikedUsers().stream()
                            .anyMatch(user -> user.getId().equals(currentUserId));
                }
                
                // 대댓글들의 좋아요 상태도 확인
                List<CommentDto.CommentResponse> replyResponses = replies.stream()
                    .map(reply -> {
                        boolean replyIsLiked = false;
                        if (currentUserId != null) {
                            replyIsLiked = reply.getLikedUsers().stream()
                                    .anyMatch(user -> user.getId().equals(currentUserId));
                        }
                        return CommentDto.CommentResponse.from(reply, currentUserId, replyIsLiked);
                    })
                    .collect(Collectors.toList());
                
                CommentDto.CommentResponse response = CommentDto.CommentResponse.from(comment, currentUserId, isLiked);
                response.setReplies(replyResponses);
                return response;
            })
            .collect(Collectors.toList());

        return new CommentDto.CommentListResponse(
                commentResponses,
                commentPage.getTotalElements(),
                commentPage.getNumber(),
                commentPage.getTotalPages(),
                commentPage.hasNext(),
                commentPage.hasPrevious()
        );
    }

    public CommentDto.CommentResponse updateComment(Long commentId, CommentDto.UpdateCommentRequest request, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!comment.getAuthorUsername().equals(username)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return CommentDto.CommentResponse.from(updatedComment, comment.getUser().getId());
    }

    public CommentDto.SimpleResponse deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!comment.getAuthorUsername().equals(username)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        // 대댓글이 있는 경우 삭제 불가
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(commentId);
        if (!replies.isEmpty()) {
            throw new IllegalArgumentException("대댓글이 있는 댓글은 삭제할 수 없습니다.");
        }

        commentRepository.delete(comment);
        return new CommentDto.SimpleResponse("댓글이 성공적으로 삭제되었습니다.", true);
    }

    public Page<CommentDto.CommentResponse> getMyComments(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByUsernameOrderByCreatedAtDesc(username, pageable);
        
        return commentPage.map(comment -> CommentDto.CommentResponse.from(comment, comment.getUser().getId()));
    }

    public CommentActionDto.UserListResponse getCommentLikes(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        List<CommentActionDto.UserInfo> users = comment.getLikedUsers().stream()
                .map(u -> new CommentActionDto.UserInfo(u.getId(), u.getUsername(), u.getNickname()))
                .collect(Collectors.toList());
        return new CommentActionDto.UserListResponse(users);
    }

    public CommentActionDto.UserListResponse getCommentReports(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        List<CommentActionDto.UserInfo> users = comment.getReportedUsers().stream()
                .map(u -> new CommentActionDto.UserInfo(u.getId(), u.getUsername(), u.getNickname()))
                .collect(Collectors.toList());
        return new CommentActionDto.UserListResponse(users);
    }

    public CommentActionDto.ActionResponse likeComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean added = comment.like(user);
        commentRepository.save(comment);

        String msg = added ? "댓글에 좋아요를 눌렀습니다." : "이미 좋아요를 누른 댓글입니다.";
        return new CommentActionDto.ActionResponse(msg, comment.getLikeCount(), true);
    }

    public CommentActionDto.ActionResponse unlikeComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean removed = comment.unlike(user);
        commentRepository.save(comment);

        String msg = removed ? "댓글 좋아요를 취소했습니다." : "좋아요를 누르지 않은 댓글입니다.";
        return new CommentActionDto.ActionResponse(msg, comment.getLikeCount(), true);
    }

    public CommentActionDto.ActionResponse reportComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean added = comment.report(user);
        commentRepository.save(comment);

        String msg = added ? "댓글을 신고했습니다." : "이미 신고한 댓글입니다.";
        return new CommentActionDto.ActionResponse(msg, comment.getReportCount(), true);
    }

    public CommentActionDto.ActionResponse unreportComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean removed = comment.unreport(user);
        commentRepository.save(comment);

        String msg = removed ? "신고가 취소되었습니다." : "해당 사용자는 이 댓글을 신고하지 않았습니다.";
        return new CommentActionDto.ActionResponse(msg, comment.getReportCount(), true);
    }

    // Comment ID로 Comment 엔티티 조회 (내부용)
    public Comment getCommentEntity(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentId));
    }
} 