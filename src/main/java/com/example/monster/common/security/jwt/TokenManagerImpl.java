package com.example.monster.common.security.jwt;

import com.example.monster.account.AccountAdapter;
import com.example.monster.account.entity.Account;
import com.example.monster.account.repository.AccountJapRepository;
import com.example.monster.account.service.AccountService;
import com.example.monster.common.redis.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.security.Key;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenManagerImpl implements TokenManager, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenManagerImpl.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    private Key key;

    @Autowired
    CookieUtil cookieUtil;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    AccountJapRepository accountJapRepository;

    public TokenManagerImpl(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.secret = secret;
//        2분
        this.accessTokenValidityTime = tokenValidityInSeconds;
//        1주일
        this.refreshTokenValidityTime = tokenValidityInSeconds * 30 * 24 * 7 ;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.printf("secret");
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    @Override
    public String createToken(Authentication authentication, TokenType tokenType) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity =  tokenType == TokenType.ACCESS_TOKEN ?  new Date(now + this.accessTokenValidityTime) : new Date(now + this.refreshTokenValidityTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .compact();
    }

    @Override
    public String refreshAccessToken(HttpServletRequest request) {
        String refreshTokenInCookie = cookieUtil.getCookie(request, TokenType.REFRESH_TOKEN.getValue()).getValue();
        if (validateRefreshToken(request)) {


            String username = redisUtil.getData(refreshTokenInCookie);
            Account account = accountJapRepository.findByUsername(username)
                    .orElseThrow(()->new UsernameNotFoundException(username));

            String authorities = new AccountAdapter(account).getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            long now = (new Date()).getTime();
            return Jwts.builder()
                    .setSubject(account.getUsername())
                    .claim(AUTHORITIES_KEY,authorities)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(now + this.accessTokenValidityTime))
                    .compact();
        }
        return null;
    }


    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();


        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String getUsername(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }
    @Override
    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
            throw e;
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
            throw e;
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
            throw e;
        }

    }

    @Override
    public boolean validateRefreshToken(HttpServletRequest request) {
        String refreshTokenInCookie = cookieUtil.getCookie(request, TokenType.REFRESH_TOKEN.getValue()).getValue();
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshTokenInCookie)
                .getBody();
        if (validateToken(refreshTokenInCookie)){
            if(new Date().before(claims.get("exp",Date.class))){
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroyTokens(HttpServletRequest request) {

    }

}
