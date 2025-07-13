package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TagDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class TagResponse {
        private Long id;
        private String name;
        private String type;
        private int postCount; // 해당 태그가 사용된 게시글 수

        public static TagResponse from(Tag tag) {
            return new TagResponse(
                tag.getId(),
                tag.getName(),
                tag.getType(),
                tag.getPosts().size()
            );
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class TagListResponse {
        private List<TagResponse> tags;
        private int totalCount;

        public static TagListResponse from(List<Tag> tags) {
            List<TagResponse> tagResponses = tags.stream()
                .map(TagResponse::from)
                .collect(Collectors.toList());
            return new TagListResponse(tagResponses, tagResponses.size());
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateTagRequest {
        private String name;
        private String type;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UpdateTagRequest {
        private String name;
        private String type;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class TagSearchRequest {
        private String keyword;
        private String type;
        private int limit = 10;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PostTagRequest {
        private List<String> tagNames; // 태그명 목록
    }
} 