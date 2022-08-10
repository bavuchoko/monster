package com.example.monster.contents.repository;

import com.example.monster.contents.Category;
import com.example.monster.contents.ContentId;
import com.example.monster.contents.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContentJpaRepository extends JpaRepository<Content, ContentId> {

    Optional<Content> findByCategory(String category);
    Page<Content> findContentByCategory(Category category, Pageable pageable);
    Optional<Content> findContentByCategoryAndId(Category category, Long id);
    

    Optional<List> findTop3ByCategoryOrderByWriteTimeDesc(Category category);

}
