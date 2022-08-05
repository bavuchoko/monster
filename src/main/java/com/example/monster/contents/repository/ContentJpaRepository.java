package com.example.monster.contents.repository;

import com.example.monster.contents.ContentId;
import com.example.monster.contents.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentJpaRepository extends JpaRepository<Content, ContentId> {

    Optional<Content> findByCategory(String category);
}
