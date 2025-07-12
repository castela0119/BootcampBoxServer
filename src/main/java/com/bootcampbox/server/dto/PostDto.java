package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        @Builder
        public CreateRequest(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        @Builder
        public UpdateRequest(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private UserDto.SimpleUserResponse user;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Post post) {
            return Response.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .user(UserDto.SimpleUserResponse.from(post.getUser()))
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }
} 