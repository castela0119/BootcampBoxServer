package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.Notification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationDto {
    @Getter
    @Setter
    public static class NotificationResponse {
        private Long id;
        private String content;
        private boolean read;
        private LocalDateTime createdAt;

        public static NotificationResponse from(Notification notification) {
            NotificationResponse dto = new NotificationResponse();
            dto.setId(notification.getId());
            dto.setContent(notification.getContent());
            dto.setRead(notification.isRead());
            dto.setCreatedAt(notification.getCreatedAt());
            return dto;
        }
    }

    @Getter
    @Setter
    public static class NotificationListResponse {
        private List<NotificationResponse> notifications;

        public static NotificationListResponse from(List<Notification> notifications) {
            NotificationListResponse dto = new NotificationListResponse();
            dto.setNotifications(notifications.stream().map(NotificationResponse::from).collect(Collectors.toList()));
            return dto;
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