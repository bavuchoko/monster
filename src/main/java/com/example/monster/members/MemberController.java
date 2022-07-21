package com.example.monster.members;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/user",  produces = MediaTypes.HAL_JSON_VALUE)
public class MemberController {

    @Autowired
    MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity signUpUser(Member member){
        Member user = memberService.saveMember(member);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberController.class).slash(user.getId());
        URI uri = selfLinkBuilder.toUri();

        EntityModel eventResource = EntityModel.of(user);
        eventResource.add(linkTo(MemberController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-events"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(uri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity loadUserList(Pageable pageable, PagedResourcesAssembler<Member> assembler){

        Page<Member> page = memberService.loadUserList(pageable);
        var pageResources = assembler.toModel(page, entity -> EntityModel.of(entity).add(linkTo(MemberController.class).withSelfRel()));
        pageResources.add(Link.of("/docs/index/html").withRel("profile"));
        System.out.printf("edsefsef");
        return ResponseEntity.ok().body(pageResources);
    }
}
