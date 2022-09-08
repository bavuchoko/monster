package com.example.monster.accounts.service;


import com.example.monster.accounts.entity.Account;
import com.example.monster.accounts.repository.AccountJapRepository;
import com.example.monster.config.redis.RedisUtil;
import com.example.monster.config.security.CustomResponseBody;
import com.example.monster.config.security.jwt.CookieUtil;
import com.example.monster.config.security.jwt.TokenManager;
import com.example.monster.config.security.jwt.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService{


    @Autowired
    AccountJapRepository accountJapRepository;
    @Autowired
    CookieUtil cookieUtil;

    @Value("${spring.jwt.token-validity-in-seconds}")
    private long accessTokenValidityTime;

    private final TokenManager tokenManager;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisUtil redisUtil;

    private final PasswordEncoder passwordEncoder;



    public Account saveAccount(Account account) {

        accountJapRepository.findByUsername(account.getUsername()).ifPresent(e->{
            throw new IllegalStateException("이미 가입된 이메일 입니다");
        });
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        return this.accountJapRepository.save(account);
    }



    public CustomResponseBody authirize(String username, String pass, HttpServletResponse response) throws BadCredentialsException {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, pass);

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);


        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenManager.createToken(authentication, TokenType.ACCESS_TOKEN);
        CustomResponseBody customResponseBody = CustomResponseBody.builder()
                .success("true")
                .token(accessToken)
                .username(authentication.getName())
                .nickname(tokenManager.getNickname(accessToken)==null ? "익명" : tokenManager.getNickname(accessToken))
                .message("로그인에 성공하였습니다.")
                .build();
        String refreshToken = tokenManager.createToken(authentication, TokenType.REFRESH_TOKEN);
        redisUtil.setData(refreshToken, authentication.getName());

        Cookie refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);
        // expires in 7 days
        refreshTokenCookie.setMaxAge((int)(accessTokenValidityTime/1000));
        response.addCookie(refreshTokenCookie);

        return customResponseBody;
    }

    public Optional<CustomResponseBody> refreshToken(HttpServletRequest request) {
        CustomResponseBody customResponseBody= null;
        //쿠키에서 refresh토큰을 꺼내 검증함
        if(tokenManager.validateRefreshToken(request)){

            String accessToken = tokenManager.refreshAccessToken(request);
            customResponseBody = CustomResponseBody.builder()
                    .success("true")
                    .token(accessToken)
                    .username(tokenManager.getUsername(accessToken))
                    .nickname(tokenManager.getNickname(accessToken) ==null ? "익명": tokenManager.getNickname(accessToken))
                    .message("토큰이 갱신되었습니다.")
                    .build();
        }
        return Optional.ofNullable(customResponseBody);
    }

    public void logout(HttpServletRequest req) {
        if(null != cookieUtil.getCookie(req, TokenType.REFRESH_TOKEN.getValue())){
        String refreshTokenInCookie = cookieUtil.getCookie(req, TokenType.REFRESH_TOKEN.getValue()).getValue();
        redisUtil.deleteData(refreshTokenInCookie);
        }
    }

    public Page<Account> loadUserList(Pageable pagable){
        return this.accountJapRepository.findAll(pagable);
    }

}
