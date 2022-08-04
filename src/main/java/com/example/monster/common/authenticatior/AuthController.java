package com.example.monster.common.authenticatior;

import com.example.monster.common.authenticatior.filter.JwtFilter;
import com.example.monster.members.MemberController;
import com.example.monster.members.MemberDto;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    AuthService authService;


    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity authorize(@Valid @RequestBody MemberDto memberDto, Errors errors, HttpServletResponse res) {
        
        if(errors.hasErrors()){
            TokenDto failToken =TokenDto.builder()
                                .code("000")
                                .token(null)
                                .username(null)
                                .message("아이디와 비밀번호를 입력하세요")
                                .build();
            return ResponseEntity.badRequest().body(EntityModel.of(failToken));
        }

        try {
            TokenDto accessToken = authService.authirize(memberDto.getUsername(), memberDto.getPassword(), res);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken.getToken());
            return new ResponseEntity(accessToken, httpHeaders, HttpStatus.OK);
        }catch (BadCredentialsException e){
            e.printStackTrace();
            TokenDto failToken =TokenDto.builder()
                    .code("003")
                    .token(null)
                    .username(null)
                    .message("이이디와 비밀번호를 확인하세요")
                    .build();
            return ResponseEntity.badRequest().body(EntityModel.of(failToken));
        }

    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest req){
        authService.logout(req);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(HttpServletRequest req,  @RequestBody TokenDto timeOutedAccessToken) {

        TokenDto newTokenDto = authService.reissue(req, timeOutedAccessToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + newTokenDto.getToken());
        return new ResponseEntity<>(newTokenDto, httpHeaders, HttpStatus.OK);
    }

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(MemberController.class).slash("/join").withRel("redirect")));
    }

}
