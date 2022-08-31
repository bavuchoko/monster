package com.example.monster.contents.repository;

import com.example.monster.contents.entity.ContentId;
import com.example.monster.contents.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ContentJpaRepository extends PagingAndSortingRepository<Content, ContentId> {

    Page<Content> findAllByOrderByWriteTimeDesc(Pageable pageable);
    Page<Content> findContentByCategoryOrderByWriteTimeDesc(String category, Pageable pageable);
    Optional<Content> findContentByCategoryAndId(String category, Long id);
    Optional<List> findTop4ByCategoryOrderByWriteTimeDesc(String category);



}
