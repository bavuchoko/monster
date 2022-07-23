package com.example.monster.members;

import com.example.monster.common.redis.RedisUtil;
import com.example.monster.common.authenticatior.CookieUtil;
import com.example.monster.common.authenticatior.JwtUtil;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/user",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final ModelMapper modelMapper;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    CookieUtil cookieUtil;

    @Autowired
    RedisUtil redisUtil;


//    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
//        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(MemberController.class).withRel("index")));
//    }

    @PostMapping("/join")
    public ResponseEntity signUpUser(MemberDto memberDto){
        Member member = modelMapper.map(memberDto, Member.class);
        Member savedUser = memberService.saveMember(member);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberController.class).slash(savedUser.getId());
        URI uri = selfLinkBuilder.toUri();

        EntityModel eventResource = EntityModel.of(savedUser);
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

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Member member,
                          HttpServletRequest req,
                          HttpServletResponse res) {
        try {
            Member user = memberService.loginUser(member.getUsername(), member.getPassword());
            MemberDto memberDto = modelMapper.map(user, MemberDto.class);
            final String token = jwtUtil.generateToken(memberDto);
            final String refreshJwt = jwtUtil.generateRefreshToken(memberDto);
            Cookie accessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
            Cookie refreshToken = cookieUtil.createCookie(JwtUtil.REFRESH_TOKEN_NAME, refreshJwt);

            redisUtil.setDataExpire(refreshJwt, memberDto.getUsername(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
            res.addCookie(accessToken);
            res.addCookie(refreshToken);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshToken.toString())
                    .body(accessToken);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e);
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

}
