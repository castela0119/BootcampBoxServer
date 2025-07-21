package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Comment;
import com.bootcampbox.server.domain.CommentReport;
import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.ReportType;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.CommentDto;
import com.bootcampbox.server.dto.CommentActionDto;
import com.bootcampbox.server.repository.CommentRepository;
import com.bootcampbox.server.repository.CommentReportRepository;
import com.bootcampbox.server.repository.PostRepository;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentReportRepository commentReportRepository;
    private final HotPostService hotPostService;
    private final NotificationService notificationService;
    private final WebSocketService webSocketService;
    
    @PersistenceContext
    private EntityManager entityManager;

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
        
        // Post의 댓글 수 업데이트
        post.updateCommentCount();
        postRepository.save(post);
        
        // HOT 점수 업데이트
        hotPostService.updatePostHotScore(postId);
        
        // 댓글 알림 생성 및 WebSocket 전송 (게시글 작성자 + 기존 댓글 작성자들에게)
        List<User> recipients = new ArrayList<>();
        recipients.add(post.getUser()); // 게시글 작성자 추가
        
        // 해당 게시글에 댓글을 단 다른 사용자들 추가
        List<User> commentUsers = commentRepository.findDistinctUsersByPostId(postId);
        for (User commentUser : commentUsers) {
            // 게시글 작성자와 중복되지 않고, 댓글 작성자 본인이 아닌 경우만 추가
            if (!commentUser.getId().equals(post.getUser().getId()) && 
                !commentUser.getId().equals(user.getId())) {
                recipients.add(commentUser);
            }
        }
        
        // 알림 전송
        webSocketService.sendCommentNotificationToAll(user, postId, recipients);
        
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
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!comment.getAuthorUsername().equals(username)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        // 수정 전 영속성 컨텍스트에서 분리
        entityManager.detach(comment);

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        
        // 수정 후 영속성 컨텍스트 정리
        entityManager.flush();
        entityManager.clear();
        
        return CommentDto.CommentResponse.from(updatedComment, comment.getUser().getId());
    }

    public CommentDto.SimpleResponse deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!comment.getAuthorUsername().equals(username)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        // 대댓글이 있는 경우 삭제 불가
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(commentId);
        if (!replies.isEmpty()) {
            throw new IllegalArgumentException("대댓글이 있는 댓글은 삭제할 수 없습니다.");
        }

        // 삭제 전 영속성 컨텍스트에서 분리
        entityManager.detach(comment);

        // 삭제 실행
        commentRepository.delete(comment);
        
        // Post의 댓글 수 업데이트
        Post post = comment.getPost();
        post.updateCommentCount();
        postRepository.save(post);
        
        // 삭제 후 영속성 컨텍스트 정리
        entityManager.flush();
        entityManager.clear();
        
        // HOT 점수 업데이트
        hotPostService.updatePostHotScore(post.getId());
        
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
        // CommentReport에서 신고 유저 목록 조회
        List<CommentReport> reports = commentReportRepository.findByCommentIdOrderByCreatedAtDesc(commentId);
        List<CommentActionDto.UserInfo> users = reports.stream()
                .map(r -> new CommentActionDto.UserInfo(r.getUser().getId(), r.getUser().getUsername(), r.getUser().getNickname()))
                .collect(Collectors.toList());
        return new CommentActionDto.UserListResponse(users);
    }

    public CommentActionDto.ActionResponse reportComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 신고했는지 확인
        boolean alreadyReported = commentReportRepository.findByCommentIdAndUserId(commentId, user.getId()).isPresent();
        if (alreadyReported) {
            return new CommentActionDto.ActionResponse("이미 신고한 댓글입니다.", comment.getReportCount(), true);
        }
        // 신고 생성
        CommentReport report = CommentReport.createReport(comment, user, ReportType.ETC, null); // 기본값
        commentReportRepository.save(report);
        // 신고 수 갱신
        comment.setReportCount(comment.getReportCount() + 1);
        commentRepository.save(comment);
        return new CommentActionDto.ActionResponse("댓글을 신고했습니다.", comment.getReportCount(), true);
    }

    public CommentActionDto.ActionResponse unreportComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Optional<CommentReport> reportOpt = commentReportRepository.findByCommentIdAndUserId(commentId, userId);
        if (reportOpt.isPresent()) {
            CommentReport report = reportOpt.get();
            commentReportRepository.delete(report);
            // 신고 수 갱신
            comment.setReportCount(Math.max(0, comment.getReportCount() - 1));
            commentRepository.save(comment);
            return new CommentActionDto.ActionResponse("신고가 취소되었습니다.", comment.getReportCount(), true);
        } else {
            return new CommentActionDto.ActionResponse("해당 사용자는 이 댓글을 신고하지 않았습니다.", comment.getReportCount(), true);
        }
    }

    public CommentActionDto.ActionResponse toggleCommentLike(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀는지 확인
        boolean alreadyLiked = comment.getLikedUsers().contains(user);
        
        if (alreadyLiked) {
            // 좋아요 취소
            boolean removed = comment.unlike(user);
            commentRepository.save(comment);

            String msg = "댓글 좋아요를 취소했습니다.";
            return new CommentActionDto.ActionResponse(msg, comment.getLikeCount(), true);
        } else {
            // 좋아요 추가
            boolean added = comment.like(user);
            commentRepository.save(comment);

            // 댓글 좋아요 알림 생성
            notificationService.sendLikeNotification(comment.getUser(), user, "comment", commentId);

            String msg = "댓글에 좋아요를 눌렀습니다.";
            return new CommentActionDto.ActionResponse(msg, comment.getLikeCount(), true);
        }
    }

    // Comment ID로 Comment 엔티티 조회 (내부용)
    public Comment getCommentEntity(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
    }

    // 게시글의 댓글 작성자들 조회
    public CommentDto.CommentAuthorsResponse getCommentAuthors(Long postId) {
        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        // 댓글 작성자들과 첫 댓글 시간 조회
        List<Object[]> results = commentRepository.findCommentAuthorsWithFirstCommentTime(postId);
        
        List<CommentDto.CommentAuthorInfo> authors = results.stream()
                .map(result -> {
                    Long userId = (Long) result[0];
                    String username = (String) result[1];
                    String nickname = (String) result[2];
                    LocalDateTime firstCommentAt = (LocalDateTime) result[3];
                    
                    return new CommentDto.CommentAuthorInfo(userId, username, nickname, firstCommentAt);
                })
                .collect(Collectors.toList());
        
        return new CommentDto.CommentAuthorsResponse(
                "댓글 작성자 조회 완료", 
                authors, 
                true
        );
    }
} 