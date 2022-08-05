package com.example.monster.contents.service;

import com.example.monster.contents.entity.Content;
import com.example.monster.contents.repository.ContentJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

    @Autowired
    ContentJpaRepository contentJpaRepository;


    public Page<Content> getContentListAll(Pageable pageable) {
        return contentJpaRepository.findAll(pageable);
    }

    public Content createContent(Content content) {
        return contentJpaRepository.save(content);
    }
}
