package com.example.monster.contents.service;

import com.example.monster.contents.entity.Content;
import com.example.monster.contents.repository.ContentJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContentService {

    @Autowired
    ContentJpaRepository contentJpaRepository;

    //Todo 제목 100자 이상 ... 대체, 내용 150글자 이상 ... 대체
    public Page<Content> getContentListAll(String category, Pageable pageable) {
        return contentJpaRepository.findContentByCategory(category, pageable);
    }

    public Content createContent(Content content) {
        return contentJpaRepository.save(content);
    }

    public Optional<Content> getSingleContent(String type, Long contendId) {
        return contentJpaRepository.findContentByCategoryAndId(type,contendId);
    }
    //Todo 제목 22자 이상 ... 대체, 내용 150글자 이상 ... 대체
    public Optional<List> getRecentContentList(String category) {
        return contentJpaRepository.findTop3ByCategoryOrderByWriteTimeDesc(category);
    }

    public void deleteContent(Content deleteContent) {

        contentJpaRepository.delete(deleteContent);
    }
}
