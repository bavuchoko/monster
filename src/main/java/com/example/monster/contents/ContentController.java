package com.example.monster.contents;


import com.example.monster.accounts.CurrentUser;
import com.example.monster.accounts.entity.Account;
import com.example.monster.contents.dto.ContentDto;
import com.example.monster.contents.entity.Content;
import com.example.monster.contents.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {


    private final ContentService contentService;
    private final ModelMapper modelMapper;

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(ContentController.class).slash("/").withRel("redirect")));
    }

    /**
     * 카테고리별 리스트 페이지
     * */
    @GetMapping("{category}")
    public ResponseEntity listPage(
            Pageable pageable,
            PagedResourcesAssembler<Content> assembler,
            @PathVariable String category,
            @CurrentUser Account account){
        Page<Content> page = this.contentService.getContentListAll(Enum.valueOf(Category.class, category.toUpperCase()), pageable);
        var pageResources = assembler.toModel(page,entity -> EntityModel.of(entity).add(linkTo(ContentController.class).slash(entity.getId()).withSelfRel()));
        pageResources.add(Link.of("/docs/ascidoc/api.html").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);
    }

    /**
     * 카테고리별 등록 페이지
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
        Content content = contentDto.of();
        content.orCategory(Enum.valueOf(Category.class, category.toUpperCase()));
//        content.orMemeber(member);
        Content savedContent = contentService.createContent(content);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(ContentController.class).slash(savedContent.getId());
        URI uri = selfLinkBuilder.toUri();
        EntityModel resources = EntityModel.of(savedContent);
        resources.add(linkTo(ContentController.class).withRel("create-contents"));
        resources.add(selfLinkBuilder.withSelfRel());
        resources.add(selfLinkBuilder.withRel("update-contents"));
        resources.add(Link.of("/docs/asciidoc/api.html#resources-content-create").withRel("profile"));
        return ResponseEntity.created(uri).body(resources);
    }

    /**
     * 카테고리별 상세 페이지
     */
    @GetMapping("{category}/{id}")
    public ResponseEntity viewContent(
            @PathVariable String category,
            @PathVariable String id,
            @CurrentUser Account account) {



        return null;
    }

}

