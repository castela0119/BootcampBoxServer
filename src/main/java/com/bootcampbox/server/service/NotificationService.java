package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Notification;
import com.bootcampbox.server.domain.NotificationHistory;
import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.NotificationDto;
import com.bootcampbox.server.repository.NotificationHistoryRepository;
import com.bootcampbox.server.repository.NotificationRepository;
import com.bootcampbox.server.repository.PostRepository;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationSettingsService settingsService;
    private final PostMuteService postMuteService;

    // 알림 목록 조회 (페이징)
    public NotificationDto.NotificationListResponse getMyNotifications(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        return NotificationDto.NotificationListResponse.from(notificationPage);
    }

    // 읽지 않은 알림 개수 조회
    public NotificationDto.UnreadCountResponse getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        long unreadCount = notificationRepository.countByUserAndIsReadFalse(user);
        return new NotificationDto.UnreadCountResponse(unreadCount);
    }

    // 특정 알림 읽음 처리
    @Transactional
    public NotificationDto.SimpleResponse markAsRead(String username, Long notificationId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        notificationRepository.markAsRead(notificationId, user);
        return new NotificationDto.SimpleResponse("알림을 읽음 처리했습니다.", true);
    }

    // 모든 알림 읽음 처리
    @Transactional
    public NotificationDto.SimpleResponse markAllAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        notificationRepository.markAllAsRead(user);
        return new NotificationDto.SimpleResponse("모든 알림을 읽음 처리했습니다.", true);
    }

    // 알림 삭제
    @Transactional
    public NotificationDto.SimpleResponse deleteNotification(String username, Long notificationId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("알림을 삭제할 권한이 없습니다.");
        }
        
        notificationRepository.delete(notification);
        return new NotificationDto.SimpleResponse("알림을 삭제했습니다.", true);
    }

    // 댓글 알림 생성 (중복 방지 포함)
    @Transactional
    public void sendCommentNotification(User recipient, User sender, Long postId) {
        try {
            // 자신의 게시글에 댓글을 달면 알림을 보내지 않음
            if (recipient.getId().equals(sender.getId())) {
                log.info("자신의 게시글 댓글 알림 차단 - 사용자: {}", sender.getUsername());
                return;
            }
            
            // 게시글 뮤트 확인
            if (postMuteService.isPostMuted(recipient.getId(), postId)) {
                log.info("뮤트된 게시글 댓글 알림 차단 - 수신자: {}, 게시글: {}", recipient.getUsername(), postId);
                return;
            }
            
            // 알림 설정 확인
            if (!settingsService.isNotificationEnabled(recipient.getId(), "comment")) {
                log.info("댓글 알림 비활성화 - 수신자: {}", recipient.getUsername());
                return;
            }
            
            // 중복 알림 방지: 24시간 내에 같은 사용자-게시글 조합으로 댓글 알림 발송 여부 확인
            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
            boolean hasRecentNotification = notificationHistoryRepository.existsCommentHistoryWithinTime(
                recipient.getId(), sender.getId(), postId, twentyFourHoursAgo);
            
            if (hasRecentNotification) {
                log.info("중복 댓글 알림 차단 - 수신자: {}, 발신자: {}, 게시글: {} (24시간 내 이미 발송됨)", 
                        recipient.getUsername(), sender.getUsername(), postId);
                return;
            }
            
            // 게시글 정보 조회
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
            
            // 알림 생성 및 저장
            Notification notification = Notification.createCommentNotification(
                recipient, sender, postId, post.getUser().getId(), post.getTitle());
            notificationRepository.save(notification);
            
            // 알림 발송 이력 저장
            var history = NotificationHistory.createCommentHistory(recipient, sender, postId);
            notificationHistoryRepository.save(history);
            
            log.info("댓글 알림 생성 완료 - 수신자: {}, 발신자: {}, 게시글: {}", 
                    recipient.getUsername(), sender.getUsername(), postId);
        } catch (Exception e) {
            log.error("댓글 알림 생성 실패: ", e);
        }
    }

    // 좋아요 알림 생성 (중복 방지 포함)
    @Transactional
    public void sendLikeNotification(User recipient, User sender, String targetType, Long targetId) {
        try {
            // 자신의 게시글/댓글에 좋아요를 누르면 알림을 보내지 않음
            if (recipient.getId().equals(sender.getId())) {
                log.info("자신의 {} 좋아요 알림 차단 - 사용자: {}", targetType, sender.getUsername());
                return;
            }
            
            // 게시글 좋아요인 경우 뮤트 확인
            if ("post".equals(targetType) && postMuteService.isPostMuted(recipient.getId(), targetId)) {
                log.info("뮤트된 게시글 좋아요 알림 차단 - 수신자: {}, 게시글: {}", recipient.getUsername(), targetId);
                return;
            }
            
            // 알림 설정 확인
            if (!settingsService.isNotificationEnabled(recipient.getId(), "like")) {
                log.info("좋아요 알림 비활성화 - 수신자: {}", recipient.getUsername());
                return;
            }
            
            // 중복 알림 방지: 24시간 내에 같은 사용자-대상 조합으로 알림 발송 여부 확인
            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
            boolean hasRecentNotification;
            
            if ("post".equals(targetType)) {
                hasRecentNotification = notificationHistoryRepository.existsLikeHistoryWithinTime(
                    recipient.getId(), sender.getId(), targetId, twentyFourHoursAgo);
            } else if ("comment".equals(targetType)) {
                hasRecentNotification = notificationHistoryRepository.existsCommentLikeHistoryWithinTime(
                    recipient.getId(), sender.getId(), targetId, twentyFourHoursAgo);
            } else {
                hasRecentNotification = false;
            }
            
            if (hasRecentNotification) {
                log.info("중복 좋아요 알림 차단 - 수신자: {}, 발신자: {}, 대상: {}:{} (24시간 내 이미 발송됨)", 
                        recipient.getUsername(), sender.getUsername(), targetType, targetId);
                return;
            }
            
            // 알림 생성 및 저장
            Notification notification = Notification.createLikeNotification(recipient, sender, targetType, targetId);
            notificationRepository.save(notification);
            
            // 알림 발송 이력 저장
            NotificationHistory history;
            if ("post".equals(targetType)) {
                history = NotificationHistory.createLikeHistory(recipient, sender, targetId);
            } else if ("comment".equals(targetType)) {
                history = NotificationHistory.createCommentLikeHistory(recipient, sender, targetId);
            } else {
                history = NotificationHistory.createLikeHistory(recipient, sender, targetId);
            }
            notificationHistoryRepository.save(history);
            
            log.info("좋아요 알림 생성 완료 - 수신자: {}, 발신자: {}, 대상: {}:{}", 
                    recipient.getUsername(), sender.getUsername(), targetType, targetId);
        } catch (Exception e) {
            log.error("좋아요 알림 생성 실패: ", e);
        }
    }

    // 시스템 알림 생성
    @Transactional
    public void sendSystemNotification(User recipient, String title, String content) {
        try {
            // 알림 설정 확인
            if (!settingsService.isNotificationEnabled(recipient.getId(), "system")) {
                log.info("시스템 알림 비활성화 - 수신자: {}", recipient.getUsername());
                return;
            }
            
            Notification notification = Notification.createSystemNotification(recipient, title, content);
            notificationRepository.save(notification);
            
            log.info("시스템 알림 생성 - 수신자: {}, 제목: {}", recipient.getUsername(), title);
        } catch (Exception e) {
            log.error("시스템 알림 생성 실패: ", e);
        }
    }

    // 모든 사용자에게 시스템 알림 발송
    @Transactional
    public void sendSystemNotificationToAll(String title, String content) {
        try {
            List<User> allUsers = userRepository.findAll();
            for (User user : allUsers) {
                sendSystemNotification(user, title, content);
            }
            
            log.info("전체 사용자 시스템 알림 발송 - 제목: {}, 수신자 수: {}", title, allUsers.size());
        } catch (Exception e) {
            log.error("전체 사용자 시스템 알림 발송 실패: ", e);
        }
    }

    // 오래된 알림 정리 (30일 이상)
    @Transactional
    public void cleanupOldNotifications() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            notificationRepository.deleteOldNotifications(cutoffDate);
            
            // 오래된 알림 이력도 함께 정리
            notificationHistoryRepository.deleteOldHistory(cutoffDate);
            
            log.info("오래된 알림 및 이력 정리 완료 - 기준일: {}", cutoffDate);
        } catch (Exception e) {
            log.error("오래된 알림 정리 실패: ", e);
        }
    }

    // 테스트 알림 생성
    @Transactional
    public NotificationDto.SimpleResponse sendTestNotification(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        sendSystemNotification(user, "테스트 알림", "이것은 테스트 알림입니다.");
        return new NotificationDto.SimpleResponse("테스트 알림을 발송했습니다.", true);
    }

    // 알림 엔티티 조회
    public Notification getNotificationEntity(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
    }
} 