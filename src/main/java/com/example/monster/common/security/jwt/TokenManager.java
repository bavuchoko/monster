package com.example.monster.common.security.jwt;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface TokenManager {

    String createToken(Authentication authentication, TokenType tokenType);
    String refreshAccessToken(HttpServletRequest request);
    boolean validateToken(String token);
    boolean validateRefreshToken(HttpServletRequest request);
    void destroyTokens(HttpServletRequest request);
    Authentication getAuthentication(String token);

    String getUsername(String accessToken);
}
