package com.example.monster.contents;

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
}
