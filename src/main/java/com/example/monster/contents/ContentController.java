package com.example.monster.contents;


import com.example.monster.members.CurrentUser;
import com.example.monster.members.Member;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Locale;

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

    @GetMapping
    public ResponseEntity mainPage(Pageable pageable, PagedResourcesAssembler<Content> assembler, @CurrentUser Member member){
        Page<Content> page = this.contentService.getContentListAll(pageable);
        var pageResources = assembler.toModel(page,entity -> EntityModel.of(entity).add(linkTo(ContentController.class).slash(entity.getId()).withSelfRel()));
        pageResources.add(Link.of("/docs/ascidoc/api.html").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);
    }

    @PostMapping("{category}")
    public ResponseEntity createContent(
            @RequestBody @Valid ContentDto contentDto, Errors errors,
            @PathVariable("category") String category,
            @CurrentUser Member member) {
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

}

