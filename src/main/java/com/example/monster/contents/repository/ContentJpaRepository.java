package com.example.monster.contents.repository;

import com.example.monster.contents.entity.ContentId;
import com.example.monster.contents.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContentJpaRepository extends JpaRepository<Content, ContentId> {

    Optional<Content> findByCategory(String category);
    Page<Content> findContentByCategory(String category, Pageable pageable);
    Optional<Content> findContentByCategoryAndId(String category, Long id);
    Optional<List> findTop4ByCategoryOrderByWriteTimeDesc(String category);



}
