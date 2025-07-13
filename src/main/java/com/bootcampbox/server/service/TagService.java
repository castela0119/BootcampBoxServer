package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Tag;
import com.bootcampbox.server.dto.TagDto;
import com.bootcampbox.server.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    // 태그 생성
    public TagDto.TagResponse createTag(TagDto.CreateTagRequest request) {
        log.info("태그 생성 요청: {}", request.getName());
        
        // 이미 존재하는 태그인지 확인
        if (tagRepository.existsByName(request.getName())) {
            throw new RuntimeException("이미 존재하는 태그입니다: " + request.getName());
        }

        Tag tag = new Tag(request.getName(), request.getType());
        Tag savedTag = tagRepository.save(tag);
        
        log.info("태그 생성 완료: {}", savedTag.getName());
        return TagDto.TagResponse.from(savedTag);
    }

    // 태그 수정
    public TagDto.TagResponse updateTag(Long tagId, TagDto.UpdateTagRequest request) {
        log.info("태그 수정 요청: {}", tagId);
        
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("태그를 찾을 수 없습니다."));

        // 이름이 변경되는 경우 중복 확인
        if (!tag.getName().equals(request.getName()) && tagRepository.existsByName(request.getName())) {
            throw new RuntimeException("이미 존재하는 태그명입니다: " + request.getName());
        }

        tag.setName(request.getName());
        tag.setType(request.getType());
        Tag updatedTag = tagRepository.save(tag);
        
        log.info("태그 수정 완료: {}", updatedTag.getName());
        return TagDto.TagResponse.from(updatedTag);
    }

    // 태그 삭제
    public void deleteTag(Long tagId) {
        log.info("태그 삭제 요청: {}", tagId);
        
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("태그를 찾을 수 없습니다."));

        // 게시글에 연결된 태그인지 확인
        if (!tag.getPosts().isEmpty()) {
            throw new RuntimeException("게시글에 연결된 태그는 삭제할 수 없습니다.");
        }

        tagRepository.delete(tag);
        log.info("태그 삭제 완료: {}", tag.getName());
    }

    // 태그 목록 조회
    public TagDto.TagListResponse getAllTags() {
        log.info("전체 태그 목록 조회");
        List<Tag> tags = tagRepository.findAll();
        return TagDto.TagListResponse.from(tags);
    }

    // 태그 타입별 조회
    public TagDto.TagListResponse getTagsByType(String type) {
        log.info("태그 타입별 조회: {}", type);
        List<Tag> tags = tagRepository.findByType(type);
        return TagDto.TagListResponse.from(tags);
    }

    // 태그 검색
    public TagDto.TagListResponse searchTags(TagDto.TagSearchRequest request) {
        log.info("태그 검색: {}", request.getKeyword());
        
        List<Tag> tags;
        if (request.getType() != null && !request.getType().isEmpty()) {
            // 타입이 지정된 경우
            tags = tagRepository.findByType(request.getType()).stream()
                    .filter(tag -> tag.getName().toLowerCase().contains(request.getKeyword().toLowerCase()))
                    .limit(request.getLimit())
                    .collect(Collectors.toList());
        } else {
            // 타입이 지정되지 않은 경우
            tags = tagRepository.findByNameContainingIgnoreCase(request.getKeyword()).stream()
                    .limit(request.getLimit())
                    .collect(Collectors.toList());
        }
        
        return TagDto.TagListResponse.from(tags);
    }

    // 인기 태그 조회
    public TagDto.TagListResponse getPopularTags(int limit) {
        log.info("인기 태그 조회 (상위 {}개)", limit);
        List<Tag> tags = tagRepository.findPopularTags().stream()
                .limit(limit)
                .collect(Collectors.toList());
        return TagDto.TagListResponse.from(tags);
    }

    // 게시글의 태그 조회
    public TagDto.TagListResponse getTagsByPostId(Long postId) {
        log.info("게시글 태그 조회: {}", postId);
        List<Tag> tags = tagRepository.findByPostId(postId);
        return TagDto.TagListResponse.from(tags);
    }

    // 태그명 목록으로 태그들 조회 (게시글 생성/수정 시 사용)
    public List<Tag> getTagsByNames(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Tag> existingTags = tagRepository.findByNameIn(tagNames);
        List<Tag> result = new ArrayList<>(existingTags);
        
        // 존재하지 않는 태그는 새로 생성
        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        
        for (String tagName : tagNames) {
            if (!existingTagNames.contains(tagName)) {
                Tag newTag = new Tag(tagName);
                Tag savedTag = tagRepository.save(newTag);
                result.add(savedTag);
                log.info("새 태그 생성: {}", tagName);
            }
        }
        
        return result;
    }

    // 태그명 목록으로 태그들 조회 (기존 태그만, 새로 생성하지 않음)
    public List<Tag> getExistingTagsByNames(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new ArrayList<>();
        }
        return tagRepository.findByNameIn(tagNames);
    }
} 