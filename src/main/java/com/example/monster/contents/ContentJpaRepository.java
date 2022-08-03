package com.example.monster.contents;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentJpaRepository extends JpaRepository<Content,ContentId> {

    Optional<Content> findByCategory(String category);
}
