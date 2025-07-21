package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Notification;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.WebSocketMessageDto;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    /**
     * 특정 사용자에게 알림 메시지 전송
     */
    public void sendNotificationToUser(String username, Notification notification) {
        try {
            // 읽지 않은 알림 개수 조회
            int unreadCount = (int) notificationService.getUnreadCount(username).getUnreadCount();
            
            // WebSocket 메시지 생성
            WebSocketMessageDto.NotificationMessage message = WebSocketMessageDto.NotificationMessage.builder()
                    .notificationId(notification.getId())
                    .senderId(notification.getSender() != null ? notification.getSender().getId() : null)
                    .senderNickname(notification.getSender() != null ? notification.getSender().getNickname() : null)
                    .notificationType(notification.getType())
                    .title(notification.getTitle())
                    .content(notification.getContent())
                    .targetType(notification.getTargetType())
                    .targetId(notification.getTargetId())
                    .read(notification.isRead())
                    .createdAt(notification.getCreatedAt())
                    .unreadCount(unreadCount)
                    .build();
            
            // 특정 사용자에게 메시지 전송
            messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
            
            log.info("WebSocket 알림 전송 완료 - 사용자: {}, 알림 ID: {}", username, notification.getId());
            
        } catch (Exception e) {
            log.error("WebSocket 알림 전송 실패 - 사용자: {}, 알림 ID: {}", username, notification.getId(), e);
        }
    }
    
    /**
     * 특정 사용자에게 읽지 않은 알림 개수 업데이트 전송
     */
    public void sendUnreadCountToUser(String username) {
        try {
            int unreadCount = (int) notificationService.getUnreadCount(username).getUnreadCount();
            
            WebSocketMessageDto.UnreadCountMessage message = WebSocketMessageDto.UnreadCountMessage.builder()
                    .unreadCount(unreadCount)
                    .build();
            
            messagingTemplate.convertAndSendToUser(username, "/queue/unread-count", message);
            
            log.info("WebSocket 읽지 않은 알림 개수 전송 완료 - 사용자: {}, 개수: {}", username, unreadCount);
            
        } catch (Exception e) {
            log.error("WebSocket 읽지 않은 알림 개수 전송 실패 - 사용자: {}", username, e);
        }
    }
    
    /**
     * 댓글 알림 생성 및 WebSocket 전송
     */
    public void sendCommentNotification(User recipient, User sender, Long postId) {
        try {
            // 기존 알림 생성 로직 실행
            notificationService.sendCommentNotification(recipient, sender, postId);
            
            // 최근 생성된 알림 조회
            var notifications = notificationService.getMyNotifications(recipient.getUsername(), 0, 1);
            if (!notifications.getNotifications().isEmpty()) {
                var latestNotification = notifications.getNotifications().get(0);
                
                // WebSocket으로 실시간 전송
                sendNotificationToUser(recipient.getUsername(), 
                    notificationService.getNotificationEntity(latestNotification.getId()));
            }
            
        } catch (Exception e) {
            log.error("댓글 알림 WebSocket 전송 실패 - 수신자: {}, 발신자: {}, 게시글: {}", 
                    recipient.getUsername(), sender.getUsername(), postId, e);
        }
    }
    
    /**
     * 좋아요 알림 생성 및 WebSocket 전송
     */
    public void sendLikeNotification(User recipient, User sender, String targetType, Long targetId) {
        try {
            // 기존 알림 생성 로직 실행
            notificationService.sendLikeNotification(recipient, sender, targetType, targetId);
            
            // 최근 생성된 알림 조회
            var notifications = notificationService.getMyNotifications(recipient.getUsername(), 0, 1);
            if (!notifications.getNotifications().isEmpty()) {
                var latestNotification = notifications.getNotifications().get(0);
                
                // WebSocket으로 실시간 전송
                sendNotificationToUser(recipient.getUsername(), 
                    notificationService.getNotificationEntity(latestNotification.getId()));
            }
            
        } catch (Exception e) {
            log.error("좋아요 알림 WebSocket 전송 실패 - 수신자: {}, 발신자: {}, 대상: {}", 
                    recipient.getUsername(), sender.getUsername(), targetType + ":" + targetId, e);
        }
    }

    /**
     * 댓글 알림 생성 및 WebSocket 전송 (게시글 작성자 + 기존 댓글 작성자들에게)
     */
    public void sendCommentNotificationToAll(User sender, Long postId, List<User> recipients) {
        try {
            for (User recipient : recipients) {
                // 자신에게는 알림을 보내지 않음
                if (recipient.getId().equals(sender.getId())) {
                    continue;
                }
                
                // 댓글 알림 생성 및 WebSocket 전송
                sendCommentNotification(recipient, sender, postId);
            }
            
            log.info("댓글 알림 전송 완료 - 발신자: {}, 게시글: {}, 수신자 수: {}", 
                    sender.getUsername(), postId, recipients.size());
            
        } catch (Exception e) {
            log.error("댓글 알림 일괄 전송 실패 - 발신자: {}, 게시글: {}", sender.getUsername(), postId, e);
        }
    }
} 