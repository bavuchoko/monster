package com.example.monster.common.authenticatior;

import com.example.monster.common.authenticatior.filter.JwtFilter;
import com.example.monster.members.MemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
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
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody MemberDto memberDto, Errors errors, HttpServletResponse res) {

        TokenDto accessToken = authService.authirize(memberDto.getUsername(), memberDto.getPassword(), res);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken.getToken());
        return new ResponseEntity<>(accessToken, httpHeaders, HttpStatus.OK);
    }


    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(HttpServletRequest req,  @RequestBody TokenDto timeOutedAccessToken) {

        TokenDto newTokenDto = authService.reissue(req, timeOutedAccessToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + newTokenDto.getToken());
        return new ResponseEntity<>(newTokenDto, httpHeaders, HttpStatus.OK);
    }

}
