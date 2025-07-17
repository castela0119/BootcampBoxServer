package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.dto.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> searchPosts(PostSearchCondition cond, Pageable pageable);
} 