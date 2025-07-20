package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.NotificationSettings;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

public class NotificationSettingsDto {
    
    @Getter
    @Setter
    public static class SettingsRequest {
        private Map<String, Boolean> settings;
    }
    
    @Getter
    @Setter
    public static class TypeSettingRequest {
        private boolean enabled;
    }
    
    @Getter
    @Setter
    public static class SettingsResponse {
        private Map<String, Boolean> settings;
        private LocalDateTime updatedAt;
        
        public static SettingsResponse from(NotificationSettings settings) {
            SettingsResponse response = new SettingsResponse();
            response.setSettings(settings.toMap());
            response.setUpdatedAt(settings.getUpdatedAt());
            return response;
        }
    }
    
    @Getter
    @Setter
    public static class TypeSettingResponse {
        private String type;
        private boolean enabled;
        private LocalDateTime updatedAt;
        
        public static TypeSettingResponse from(NotificationSettings settings, String type) {
            TypeSettingResponse response = new TypeSettingResponse();
            response.setType(type);
            
            switch (type.toLowerCase()) {
                case "comment":
                    response.setEnabled(settings.isCommentEnabled());
                    break;
                case "like":
                    response.setEnabled(settings.isLikeEnabled());
                    break;
                case "follow":
                    response.setEnabled(settings.isFollowEnabled());
                    break;
                case "system":
                    response.setEnabled(settings.isSystemEnabled());
                    break;
                default:
                    throw new IllegalArgumentException("알 수 없는 알림 타입: " + type);
            }
            
            response.setUpdatedAt(settings.getUpdatedAt());
            return response;
        }
    }
    
    @Getter
    @Setter
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        private Object error;
        
        public static ApiResponse success(String message, Object data) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(true);
            response.setMessage(message);
            response.setData(data);
            response.setError(null);
            return response;
        }
        
        public static ApiResponse error(String message, Object error) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(false);
            response.setMessage(message);
            response.setData(null);
            response.setError(error);
            return response;
        }
    }
} 