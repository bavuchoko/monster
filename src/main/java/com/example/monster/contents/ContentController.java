package com.example.monster.contents;


import com.example.monster.accounts.CurrentUser;
import com.example.monster.accounts.entity.Account;
import com.example.monster.contents.dto.ContentDto;
import com.example.monster.contents.entity.Content;
import com.example.monster.contents.entity.ContentImage;
import com.example.monster.contents.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    @Value("${urlPath}")
    private String urlPath;



    private final ContentService contentService;
    private final ModelMapper modelMapper;

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(ContentController.class).slash("/").withRel("redirect")));
    }

    /**
     * 리스트 조회
     * */
    @GetMapping("{category}")
    public ResponseEntity listPage(
            Pageable pageable,
            PagedResourcesAssembler<Content> assembler,
            @PathVariable String category,
            @CurrentUser Account account){

        Page<Content> page = this.contentService.getContentListAll(category, pageable);
        var pageResources = assembler.toModel(page,entity -> EntityModel.of(entity).add(linkTo(ContentController.class).slash(entity.getId()).withSelfRel()));
        pageResources.add(Link.of("/docs/ascidoc/api.html").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);
    }

    /**
     * 최근  조회
     * * */
    @GetMapping("/recent/{category}")
    public ResponseEntity recentPage(
            @PathVariable String category) {

        Optional<List> recentContentList = this.contentService.getRecentContentList(category);
        if (recentContentList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List list = (List)recentContentList.get().stream().map(recent -> {
            EntityModel<Content> entity = EntityModel.of((Content) recent);
            entity.add(linkTo(ContentController.class).slash(category).slash(((Content) recent).getId()).withSelfRel());
            return entity;
        }).collect(Collectors.toList());
        CollectionModel resource = CollectionModel.of(list);
        resource.add(Link.of("/docs/asciidoc/api.html#resources-content-create").withRel("profile"));
        return ResponseEntity.ok().body(resource);
    }


    /**
     * 단건 조회
     *
     */
    @GetMapping("{category}/{id}")
    public ResponseEntity viewContent(
            @PathVariable String category,
            @PathVariable String id,
            @CurrentUser Account account) {

        Long contendId = Long.valueOf(id);

        Optional<Content> singleContent = contentService.getSingleContent(category, contendId);
        if(singleContent.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Content loadedContent = singleContent.get();
        WebMvcLinkBuilder selfLinkBuilder = linkTo(ContentController.class).slash(loadedContent.getCategory()).slash(loadedContent.getId());
        EntityModel resource = EntityModel.of(loadedContent);
        resource.add(selfLinkBuilder.withSelfRel());
        resource.add(selfLinkBuilder.withRel("update"));
        resource.add(Link.of("/docs/asciidoc/api.html#resources-content-create").withRel("profile"));

        return ResponseEntity.ok().body(resource);
    }

    /**
     * 등록
     * */
    @PostMapping("{category}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity createContent(
            @RequestBody @Valid ContentDto contentDto, Errors errors,
            @PathVariable String category,
            @CurrentUser Account account) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        Content content = contentDto.toEntity();
        content.categorySetter(category);
//        content.orMemeber(member);
        Content savedContent = contentService.createContent(content);
        
        WebMvcLinkBuilder selfLinkBuilder = linkTo(ContentController.class).slash(savedContent.getCategory()).slash(savedContent.getId());
        URI uri = selfLinkBuilder.toUri();
        
        EntityModel resources = EntityModel.of(savedContent);
        
        resources.add(selfLinkBuilder.withSelfRel());
        resources.add(linkTo(ContentController.class).slash(savedContent.getCategory()).withRel("list"));
        resources.add(Link.of("/docs/asciidoc/api.html#resources-content-create").withRel("profile"));
        
        return ResponseEntity.created(uri).body(resources);
    }

    /**
     * 수정
     * @param contentDto
     * @param errors
     * @param category
     * @param id
     * @param account
     * @return
     */

    @PutMapping("{category}/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity updateContent(
            @RequestBody @Valid ContentDto contentDto, Errors errors,
            @PathVariable String category,
            @PathVariable String id,
            @CurrentUser Account account) {
        Long contentId = Long.valueOf(id);
        Optional<Content> loadedConetnt = contentService.getSingleContent(category,contentId);

        if(loadedConetnt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        Content contentEntity = loadedConetnt.get();

        if (!contentEntity.getAccount().equals(account)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("aa");
        }

        Content contentForUpdate = contentService.createContent(contentDto.toEntity());
        EntityModel resource = EntityModel.of(contentForUpdate);
        resource.add(Link.of("/docs/index.html#resources-content-update").withRel("profile"));
        return ResponseEntity.ok(resource);
    }


    @DeleteMapping("{category}/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity deleteContent(
            @PathVariable String category,
            @PathVariable String id,
            @CurrentUser Account account) {

        Long contentId = Long.valueOf(id);
        Optional<Content> loadedConetnt = contentService.getSingleContent(category, contentId);

        if(loadedConetnt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Content deleteContent = loadedConetnt.get();

        if (!deleteContent.getAccount().equals(account)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("aa");
        }
        contentService.deleteContent(deleteContent);
        EntityModel resource = EntityModel.of(deleteContent);
        return ResponseEntity.ok(resource);
    }


    @PostMapping("image")
//    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity imageUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if(file.isEmpty()){
//            //todo 파일 안왔을때
        }

        String savedFileName = contentService.uploadImage(file);
        String imagePath = urlPath +savedFileName;

        return ResponseEntity.ok().body(imagePath);
    }

}

