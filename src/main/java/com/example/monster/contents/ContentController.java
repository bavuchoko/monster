package com.example.monster.contents;


import com.example.monster.members.CurrentUser;
import com.example.monster.members.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    @Autowired
    ContentService contentService;

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(ContentController.class).slash("/").withRel("redirect")));
    }

    @GetMapping
    public ResponseEntity mainPage(Pageable pageable, PagedResourcesAssembler<Content> assembler, @CurrentUser Member member){
        Page<Content> page = this.contentService.getContentListAll(pageable);
        var pageResources = assembler.toModel(page,entity -> EntityModel.of(entity).add(linkTo(ContentController.class).slash(entity.getId()).withSelfRel()));
        pageResources.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);
    }
}

