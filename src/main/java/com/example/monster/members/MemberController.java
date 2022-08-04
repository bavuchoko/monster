package com.example.monster.members;

import com.example.monster.common.authenticatior.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/user",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final ModelMapper modelMapper;

    @Autowired
    CookieUtil cookieUtil;


    @GetMapping
    public ResponseEntity loadUserList(Pageable pageable, PagedResourcesAssembler<Member> assembler){

        Page<Member> page = memberService.loadUserList(pageable);
        var pageResources = assembler.toModel(page, entity -> EntityModel.of(entity).add(linkTo(MemberController.class).withSelfRel()));
        pageResources.add(Link.of("/docs/index/html").withRel("profile"));
        System.out.printf("edsefsef");
        return ResponseEntity.ok().body(pageResources);
    }


    @PostMapping("/join")


    //Todo 추후 Errors를 좀더 커스텀하고싶음.
//    private ResponseEntity<EntityModel<CustomException>> badRequest(CustomException errors) {
//        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(MemberController.class).withRel("index")));
//    }


    @GetMapping("/permitted")
    public String permitted(){
        return "everybody permiited";
    }


    @GetMapping("/admintest")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String admintest(){
        return "only admin permiited";
    }

    @GetMapping("/usertest")
    @PreAuthorize("hasAnyRole('USER')")
    public String usertest(){
        return "only user permiited";
    }

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(MemberController.class).slash("/join").withRel("redirect")));
    }
}
