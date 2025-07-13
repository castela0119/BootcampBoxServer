package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.Tag;
import com.bootcampbox.server.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        private List<String> tagNames; // 태그명 목록
        private boolean isAnonymous = false; // 익명 여부
        private String authorUserType; // 작성 당시 사용자의 신분 type

        @Builder
        public CreateRequest(String title, String content, List<String> tagNames, boolean isAnonymous, String authorUserType) {
            this.title = title;
            this.content = content;
            this.tagNames = tagNames;
            this.isAnonymous = isAnonymous;
            this.authorUserType = authorUserType;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        private List<String> tagNames; // 태그명 목록
        private boolean isAnonymous = false; // 익명 여부
        private String authorUserType; // 작성 당시 사용자의 신분 type

        @Builder
        public UpdateRequest(String title, String content, List<String> tagNames, boolean isAnonymous, String authorUserType) {
            this.title = title;
            this.content = content;
            this.tagNames = tagNames;
            this.isAnonymous = isAnonymous;
            this.authorUserType = authorUserType;
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
        
        // 익명 관련 필드
        @JsonProperty("isAnonymous")
        private boolean isAnonymous;
        private String anonymousNickname;
        private String displayNickname; // 표시할 닉네임 (익명이면 익명닉네임, 아니면 실제 닉네임)
        private boolean canBeModified; // 수정 가능 여부
        private boolean canBeDeleted; // 삭제 가능 여부
        private String authorUserType; // 작성 당시 사용자의 신분 type
        private List<String> tagNames; // 태그명 목록

        public static Response from(Post post) {
            String displayNickname = post.isAnonymous() ? 
                post.getAnonymousNickname() : 
                post.getUser().getNickname();
                
            List<String> tagNames = post.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());

            return Response.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .user(post.isAnonymous() ? null : UserDto.SimpleUserResponse.from(post.getUser())) // 익명이면 작성자 정보 숨김
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .isAnonymous(post.isAnonymous())
                    .anonymousNickname(post.getAnonymousNickname())
                    .displayNickname(displayNickname)
                    .canBeModified(post.canBeModified())
                    .canBeDeleted(post.canBeDeleted())
                    .authorUserType(post.getAuthorUserType()) // 작성 당시 신분 type
                    .tagNames(tagNames)
                    .build();
        }
    }
} 