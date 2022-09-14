package com.example.monster.contents;


import com.example.monster.accounts.CurrentUser;
import com.example.monster.accounts.entity.Account;
import com.example.monster.contents.dto.ContentDto;
import com.example.monster.contents.dto.RepliesDto;
import com.example.monster.contents.entity.Content;
import com.example.monster.contents.entity.ContentImage;
import com.example.monster.contents.entity.Replies;
import com.example.monster.contents.repository.ContentRepositorySupport;
import com.example.monster.contents.service.ContentResources;
import com.example.monster.contents.service.ContentService;
import com.example.monster.contents.service.RepliesService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
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
@RequestMapping(value = "/api/content" , produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class ContentController {

    @Value("${urlPath}")
    private String urlPath;



    private final ContentService contentService;
    private final RepliesService repliesService;
    private final ContentRepositorySupport ContentRepositorySupport;
    private final ModelMapper modelMapper;

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(ContentController.class).slash("/").withRel("redirect")));
    }

    /**
     * 전체 리스트 조회
     * */
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity listAllPage(
            Pageable pageable,
            PagedResourcesAssembler<Content> assembler,
            @CurrentUser Account account){
//       Page<Content> page = contentService.getContentListAll(pageable);
        Page<Content> page = this.ContentRepositorySupport.quertFindOrderByWriteTimeDesc(pageable);
        var pageResources = assembler.toModel(page,entity -> EntityModel.of(entity).add(linkTo(ContentController.class).slash(entity.getCategory()).withRel("query-content")));
        pageResources.add(Link.of("/docs/ascidoc/api.html").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);
    }
    /**
     * 카테고리 리스트 조회
     * */
    @GetMapping("{category}")
    @PreAuthorize("permitAll()")
    public ResponseEntity listPage(
            Pageable pageable,
            PagedResourcesAssembler<Content> assembler,
            @PathVariable String category,
            @CurrentUser Account account){

        Page<Content> page = this.contentService.getContentCategoryListAll(category, pageable);
        var pageResources = assembler.toModel(page,entity ->
                EntityModel.of(entity).
                        add(linkTo(ContentController.class).slash(entity.getCategory()).withRel("query-content")).
                        add(linkTo(ContentController.class).slash(entity.getCategory()).slash(entity.getId()).withSelfRel())
        );

        pageResources.add(Link.of("/docs/ascidoc/api.html").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);
    }

    /**
     * 최근  조회
     * * */
    @GetMapping("/recent/{category}")
    @PreAuthorize("permitAll()")
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
    @PreAuthorize("permitAll()")
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


        ContentResources replyAssembling = new ContentResources(loadedContent, account);
        EntityModel resource = EntityModel.of(replyAssembling);
        resource.add(Link.of("/docs/asciidoc/api.html#resources-query-content").withRel("profile"));
        if( loadedContent.getAccount().equals(account)){
            resource.add(selfLinkBuilder.withRel("update"));
        }
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
        content.orMemeber(account);
        Content savedContent = contentService.createContent(content);
        
        WebMvcLinkBuilder selfLinkBuilder = linkTo(ContentController.class).slash(savedContent.getCategory()).slash(savedContent.getId());
        URI uri = selfLinkBuilder.toUri();
        
        EntityModel resources = EntityModel.of(savedContent);
        
        resources.add(linkTo(ContentController.class).slash(savedContent.getCategory()).withRel("query-content"));
        resources.add(selfLinkBuilder.withSelfRel());
        resources.add(selfLinkBuilder.withRel("update-content"));
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
        Optional<Content> singleContent = contentService.getSingleContent(category,contentId);

        if(singleContent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Content loadedConetnt = singleContent.get();


        Account ac = loadedConetnt.getAccount();
        Account ac2 = account;
        String a = ac.equals(ac2) ? "Y":"N";
        if (!loadedConetnt.getAccount().equals(account)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("aa");
        }

        loadedConetnt.changeTitle(contentDto.getTitle());
        loadedConetnt.changeBody(contentDto.getBody());
        loadedConetnt.changeBodyHtml(contentDto.getBodyHtml());
        loadedConetnt.changeUpdateTime(contentDto.getUpdateTime());
        contentService.createContent(loadedConetnt);
        return ResponseEntity.noContent().build();
    }

    /**
     * 삭제
     * @param category
     * @param id
     * @param account
     * @return
     */
    @DeleteMapping("{category}/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity deleteContent(
            @PathVariable String category,
            @PathVariable String id,
            @CurrentUser Account account) {

        Long contentId = Long.valueOf(id);
        Optional<Content> singleContent = contentService.getSingleContent(category, contentId);

        if(singleContent.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Content loadedConetnt = singleContent.get();

        if (!loadedConetnt.getAccount().equals(account)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("aa");
        }
        contentService.deleteContent(loadedConetnt);
        return ResponseEntity.noContent().build();
    }


    /**
     * 이미지 업로드
     * @param file
     * @return res.data 업로드된 이미지 파일 주소
     * @throws IOException
     */
    @PostMapping("image")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity imageUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if(file.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        String savedFileName = contentService.uploadImage(file);
        String imagePath = urlPath +savedFileName;

        return ResponseEntity.ok().body(imagePath);
    }

    @GetMapping("sagong")
    @PreAuthorize("hasAnyRole('USER')")
    public void sagong(@CurrentUser Account account) {
        Account aa = account;
    }

    
    /**
     * 댓글 등록
     * */
    @PostMapping("{category}/{id}/reply")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity addReply(
            @RequestBody @Valid RepliesDto replies, Errors errors,
            @PathVariable String category,
            @PathVariable String id,
            @CurrentUser Account account ) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        if (!StringUtils.hasText(category) || !StringUtils.hasText(id)) {
            return ResponseEntity.badRequest().build();
        }

        Long contentId = Long.valueOf(id);
        Optional<Content> singleContent = contentService.getSingleContent(category, contentId);

        if (singleContent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Content loadedContent = singleContent.get();
        Replies reply = replies.toEntity();
        reply.contentSetter(loadedContent);
        reply.accountSetter(account);
        repliesService.writeReply(reply);
        return  ResponseEntity.ok().build();
    }


    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{category}/{contendId}/reply/{replyId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity removeReply(
            @PathVariable String category,
            @PathVariable String contendId,
            @PathVariable String replyId,
            @CurrentUser Account account) {

        if (!StringUtils.hasText(replyId)||!StringUtils.hasText(category)||!StringUtils.hasText(contendId)) {
            return ResponseEntity.badRequest().build();
        }

        Long cId = Long.valueOf(contendId);
        Long rId = Long.valueOf(replyId);
        Optional<Content> singleContent = contentService.getSingleContent(category, cId);
        Optional<Replies> repliesOptional = repliesService.findById(rId);

        if (singleContent.isEmpty() || repliesOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Replies replies = repliesOptional.get();
        if (!replies.getAccount().equals(account)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        repliesService.removeReply(replies);
        return ResponseEntity.noContent().build();
    }


    /**
     * 댓글 수정
     * */
}

