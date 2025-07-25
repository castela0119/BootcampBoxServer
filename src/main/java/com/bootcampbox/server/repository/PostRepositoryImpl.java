package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.QPost;
import com.bootcampbox.server.domain.QTag;
import com.bootcampbox.server.domain.QUser;
import com.bootcampbox.server.dto.PostSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> searchPosts(PostSearchCondition cond, Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QTag tag = QTag.tag;

        BooleanBuilder builder = new BooleanBuilder();

        // 제목/내용 검색 (2글자 이상, OR)
        if (cond.getKeyword() != null && cond.getKeyword().trim().length() >= 2) {
            builder.and(
                post.title.contains(cond.getKeyword())
                    .or(post.content.contains(cond.getKeyword()))
            );
        }

        // 작성자 유형
        if (cond.getUserType() != null && !cond.getUserType().isEmpty()) {
            builder.and(post.user.userType.eq(cond.getUserType()));
        }

        // 태그 AND 조건 (부분검색)
        if (cond.getTagList() != null && !cond.getTagList().isEmpty()) {
            for (String tagName : cond.getTagList()) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    builder.and(post.tags.any().name.contains(tagName.trim()));
                }
            }
        }

        // 정렬
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(cond.getSortBy(), post);

        // 쿼리
        JPAQuery<Post> query = queryFactory.selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.tags, tag).fetchJoin()
                .where(builder)
                .distinct();
        if (orderSpecifier != null) {
            query.orderBy(orderSpecifier);
        }

        // 페이징
        List<Post> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        long total = queryFactory.selectFrom(post)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, QPost post) {
        if (sort == null) return post.createdAt.desc();
        switch (sort) {
            case "likes":
                return post.likeCount.desc();
            case "views":
                return post.viewCount.desc();
            case "comments":
                return post.commentCount.desc();
            case "createdAt":
            default:
                return post.createdAt.desc();
        }
    }
} 