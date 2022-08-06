package com.example.monster.accounts;

import com.example.monster.accounts.dto.AccountDto;
import com.example.monster.accounts.entity.Account;
import com.example.monster.accounts.service.AccountService;
import com.example.monster.config.security.CustomResponseBody;
import com.example.monster.config.security.filter.JwtFilter;
import com.example.monster.config.security.jwt.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/user",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;



    @Autowired
    CookieUtil cookieUtil;


    @GetMapping
    public ResponseEntity loadUserList(Pageable pageable, PagedResourcesAssembler<Account> assembler){

        Page<Account> page = accountService.loadUserList(pageable);
        var pageResources = assembler.toModel(page, entity -> EntityModel.of(entity).add(linkTo(AccountController.class).withSelfRel()));
        pageResources.add(Link.of("/docs/index/html").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/authenticate")
    public ResponseEntity authorize(@Valid @RequestBody AccountDto accountDto, Errors errors, HttpServletResponse response) {

        if(errors.hasErrors()){
            return badRequest(errors);
        }
        try {
            CustomResponseBody accessToken = accountService.authirize(accountDto.getUsername(), accountDto.getPassword(), response);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken.getToken());
            return new ResponseEntity(accessToken, httpHeaders, HttpStatus.OK);
        }catch (BadCredentialsException e){
            e.printStackTrace();
            CustomResponseBody failToken = CustomResponseBody.builder()
                    .success("fail")
                    .token(null)
                    .username(null)
                    .message("이이디와 비밀번호를 확인하세요")
                    .build();
            return ResponseEntity.badRequest().body(EntityModel.of(failToken));
        }
    }

    @GetMapping("/refreshtoken")
    public ResponseEntity refreshToken(HttpServletRequest request) {
        if(accountService.refreshToken(request).isPresent()){
            CustomResponseBody refreshedAccessToken =accountService.refreshToken(request).get();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + refreshedAccessToken.getToken());
            return new ResponseEntity(refreshedAccessToken, httpHeaders, HttpStatus.OK);
        }
        CustomResponseBody failToken = CustomResponseBody.builder()
                .success("fail")
                .token(null)
                .username(null)
                .message("토큰이 만료되었습니다.")
                .build();
        return ResponseEntity.badRequest().body(EntityModel.of(failToken));
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest req){
        accountService.logout(req);
    }




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
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(AccountController.class).slash("/join").withRel("redirect")));
    }
}
