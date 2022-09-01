package com.example.monster.contents.repository;

import com.example.monster.contents.entity.Content;
import com.example.monster.contents.entity.QContent;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContentRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    QContent content = new QContent("content");

    public ContentRepositorySupport(JPAQueryFactory queryFactory) {
        super(Content.class);
        this.queryFactory = queryFactory;
    }

    public Page<Content> quertFindOrderByWriteTimeDesc(Pageable pageable) {
        JPQLQuery<Content> query = queryFactory.selectFrom(content)
                .orderBy(content.writeTime.desc());

        long totalCount = query.fetchCount();
        List<Content> result = getQuerydsl().applyPagination(pageable,query).fetch();
        return new PageImpl<>(result,pageable,totalCount);
    }
}
