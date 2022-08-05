package com.example.monster.account.service;


import com.example.monster.account.AccountAdapter;
import com.example.monster.account.entity.Account;
import com.example.monster.account.repository.AccountJapRepository;
import com.example.monster.common.redis.RedisUtil;
import com.example.monster.common.security.CustomResponseBody;
import com.example.monster.common.security.jwt.CookieUtil;
import com.example.monster.common.security.jwt.TokenManager;
import com.example.monster.common.security.jwt.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {


    @Autowired
    AccountJapRepository accountJapRepository;
    @Autowired
    CookieUtil cookieUtil;

    private final TokenManager tokenManager;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisUtil redisUtil;

    private final PasswordEncoder passwordEncoder;



    public Account saveMember(Account account) {
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        return this.accountJapRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountJapRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException(username));
        return new AccountAdapter(account);
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
                .message("로그인에 성공하였습니다.")
                .build();
        String refreshToken = tokenManager.createToken(authentication, TokenType.REFRESH_TOKEN);
        redisUtil.setData(refreshToken, authentication.getName());
        Cookie refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken);
        response.addCookie(refreshTokenCookie);

        return customResponseBody;
    }

    public Optional<CustomResponseBody> refreshToken(HttpServletRequest request) {
        CustomResponseBody customResponseBody= null;
        if(tokenManager.validateRefreshToken(request)){
            String accessToken = tokenManager.refreshAccessToken(request);
            customResponseBody = CustomResponseBody.builder()
                    .success("true")
                    .token(accessToken)
                    .username(tokenManager.getUsername(accessToken))
                    .message("토큰이 갱신되었습니다.")
                    .build();
        }
        return Optional.ofNullable(customResponseBody);
    }

    public void logout(HttpServletRequest req) {
        String refreshTokenInCookie = cookieUtil.getCookie(req, TokenType.REFRESH_TOKEN.getValue()).getValue();
        redisUtil.deleteData(refreshTokenInCookie);
    }




    public Page<Account> loadUserList(Pageable pagable){
        return this.accountJapRepository.findAll(pagable);
    }

}
