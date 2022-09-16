package com.example.monster.contents.service;

import com.example.monster.contents.entity.Content;
import com.example.monster.contents.repository.ContentJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ContentService {

    @Value("${imageResourcesPath}")
    private String resourcesPath;

    @Value("${savePath}")
    private String savePath;

    @Autowired
    ContentJpaRepository contentJpaRepository;

    public Page<Content> getContentListAll(Pageable pageable) {
        return contentJpaRepository.findAllByOrderByWriteTimeDesc(pageable);
    }


    //Todo 제목 100자 이상 ... 대체, 내용 150글자 이상 ... 대체
    public Page<Content> getContentCategoryListAll(String category, Pageable pageable) {
        return contentJpaRepository.findContentByCategoryOrderByWriteTimeDesc(category, pageable);
    }

    public Content createContent(Content content) {
        return contentJpaRepository.save(content);
    }

    public Optional<Content> getSingleContent(String type, Long contendId) {
        return contentJpaRepository.findContentByCategoryAndId(type,contendId);
    }
    //Todo 제목 22자 이상 ... 대체, 내용 150글자 이상 ... 대체
    public Optional<List> getRecentContentList(String category) {
        return contentJpaRepository.findTop4ByCategoryOrderByWriteTimeDesc(category);
    }

    public void deleteContent(Content deleteContent) {
        contentJpaRepository.delete(deleteContent);
    }

    public String uploadImage(MultipartFile file) throws IOException {

        String uuid = UUID.randomUUID().toString();

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        System.out.println("=================");
        System.out.println("=================");
        System.out.println("=================");
        System.out.println("=================");
        System.out.println("=================");
        System.out.println("originalFileName : "+originalFileName);
        System.out.println("savePath : " +savePath);

        File folder = new File(savePath);
        if(!folder.exists()){
            try {
                folder.mkdir();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        String savedFileName= uuid + "_" + originalFileName;
        System.out.println("=================");
        System.out.println("=================");
        System.out.println("=================");
        System.out.println("=================");
        System.out.println("=================");
        System.out.println("savedFileName : " + savedFileName);
        file.transferTo(Paths.get(savePath + savedFileName));

        return savedFileName;
    }


}
