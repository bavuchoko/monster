package com.example.monster.common.authenticatior;

import com.example.monster.common.redis.RedisUtil;
import com.example.monster.members.Member;
import com.example.monster.members.MemberJapRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    CookieUtil cookieUtil;

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisUtil redisUtil;


    public TokenDto authirize(String username, String pass, HttpServletResponse res) throws BadCredentialsException{

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, pass);

            Authentication authentication = authenticationManagerBuilder.getObject()
                    .authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = tokenProvider.createToken(authentication, TokenType.ACCESS_TOKEN);
            TokenDto tokenDto = TokenDto.builder()
                    .code("200")
                    .token(accessToken)
                    .username(authentication.getName())
                    .message("로그인에 성공하였습니다.")
                    .build();

            String refreshToken = tokenProvider.createToken(authentication, TokenType.REFRESH_TOKEN);
            redisUtil.setData(refreshToken, authentication.getName());
            Cookie refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken);
            res.addCookie(refreshTokenCookie);

        return tokenDto;
    }

    public TokenDto reissue(HttpServletRequest req, TokenDto timeOutedAccessToken) {
        System.out.printf(timeOutedAccessToken.getToken());
        String username = tokenProvider.getAuthentication(timeOutedAccessToken.getToken()).getName();
        String refreshTokenInCookie = cookieUtil.getCookie(req, TokenType.REFRESH_TOKEN.getValue()).getValue();
        //사용자가 다름
        if(tokenProvider.validateToken(refreshTokenInCookie, req) && compareRefreshTokenOwner(username, refreshTokenInCookie)){
            Authentication authentication = tokenProvider.getAuthentication(refreshTokenInCookie);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String newAccessToken = tokenProvider.createToken(authentication, TokenType.ACCESS_TOKEN);

            return TokenDto.builder()
                    .token(newAccessToken)
                    .username(authentication.getName())
                    .build();
        }
        if (!compareRefreshTokenOwner(username, refreshTokenInCookie)) {
            //Todo exception추가
            return null;
        }
        //유효하지 않은 토큰
        if (!tokenProvider.validateToken(refreshTokenInCookie, req)) {
            //Todo exception추가
            return null;
        }

        return null;
    }

    public void logout(HttpServletRequest req) {
        String refreshTokenInCookie = cookieUtil.getCookie(req, TokenType.REFRESH_TOKEN.getValue()).getValue();
        redisUtil.deleteData(refreshTokenInCookie);
    }


    public Boolean compareRefreshTokenOwner(String username, String refreshTokenInCookie){
        return username.equals(redisUtil.getData(refreshTokenInCookie)) ? true : false;
    }
}

