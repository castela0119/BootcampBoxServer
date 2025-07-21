package com.bootcampbox.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class WebSocketMessageDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationMessage {
        @Builder.Default
        private String type = "notification";
        private Long notificationId;
        private Long senderId;
        private String senderNickname;
        private String notificationType;
        private String title;
        private String content;
        private String targetType;
        private Long targetId;
        private boolean read;
        private LocalDateTime createdAt;
        private int unreadCount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnreadCountMessage {
        @Builder.Default
        private String type = "unread_count";
        private int unreadCount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectionMessage {
        @Builder.Default
        private String type = "connection";
        private String status; // "connected", "disconnected"
        private String username;
    }
} 