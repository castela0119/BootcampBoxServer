package com.bootcampbox.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class PostActionDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ActionResponse {
        private String message;
        private int count;
        private boolean isLiked;
        private boolean isReported;
        private boolean isBookmarked;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UserListResponse {
        private List<Long> userIds;
        private int totalCount;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ReportRequest {
        private String reason;
    }
} 