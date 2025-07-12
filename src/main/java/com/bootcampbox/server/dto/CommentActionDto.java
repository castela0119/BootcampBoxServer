package com.bootcampbox.server.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

public class CommentActionDto {
    @Getter
    @Setter
    public static class ActionResponse {
        private String message;
        private int count;
        private boolean success;
        public ActionResponse(String message, int count, boolean success) {
            this.message = message;
            this.count = count;
            this.success = success;
        }
    }

    @Getter
    @Setter
    public static class UserListResponse {
        private List<UserInfo> users;
        private int count;
        public UserListResponse(List<UserInfo> users) {
            this.users = users;
            this.count = users.size();
        }
    }

    @Getter
    @Setter
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        public UserInfo(Long id, String username, String nickname) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
        }
    }
} 