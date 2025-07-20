package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.Notification;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationDto {
    @Getter
    @Setter
    public static class NotificationResponse {
        private Long id;
        private Long senderId;
        private String senderNickname;
        private String type;
        private String title;
        private String content;
        private String targetType;
        private Long targetId;
        private boolean read;
        private LocalDateTime createdAt;

        public static NotificationResponse from(Notification notification) {
            NotificationResponse dto = new NotificationResponse();
            dto.setId(notification.getId());
            dto.setSenderId(notification.getSender() != null ? notification.getSender().getId() : null);
            dto.setSenderNickname(notification.getSender() != null ? notification.getSender().getNickname() : null);
            dto.setType(notification.getType());
            dto.setTitle(notification.getTitle());
            dto.setContent(notification.getContent());
            dto.setTargetType(notification.getTargetType());
            dto.setTargetId(notification.getTargetId());
            dto.setRead(notification.isRead());
            dto.setCreatedAt(notification.getCreatedAt());
            return dto;
        }
    }

    @Getter
    @Setter
    public static class NotificationListResponse {
        private List<NotificationResponse> notifications;
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private boolean hasNext;
        private boolean hasPrevious;

        public static NotificationListResponse from(Page<Notification> notificationPage) {
            NotificationListResponse dto = new NotificationListResponse();
            dto.setNotifications(notificationPage.getContent().stream()
                    .map(NotificationResponse::from)
                    .collect(Collectors.toList()));
            dto.setCurrentPage(notificationPage.getNumber());
            dto.setTotalPages(notificationPage.getTotalPages());
            dto.setTotalElements(notificationPage.getTotalElements());
            dto.setHasNext(notificationPage.hasNext());
            dto.setHasPrevious(notificationPage.hasPrevious());
            return dto;
        }
    }

    @Getter
    @Setter
    public static class UnreadCountResponse {
        private long unreadCount;

        public UnreadCountResponse(long unreadCount) {
            this.unreadCount = unreadCount;
        }
    }

    @Getter
    @Setter
    public static class SimpleResponse {
        private String message;
        private boolean success;
        
        public SimpleResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }
    }
} 