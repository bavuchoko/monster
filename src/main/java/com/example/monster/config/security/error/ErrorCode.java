package com.example.monster.config.security.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOACCESS_TOKEN(HttpStatus.BAD_REQUEST,"no access token : 존재하지 않는 토큰."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "unsupported token : 지원하지 않는 토큰."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "token expired : 토큰 유효기간 만료."),
    ILLEGALARGU_TOKEN(HttpStatus.BAD_REQUEST, "illegal argument token :  부적절한 토큰.");

    private final HttpStatus status;
    private final String message;

    ErrorCode (HttpStatus status, String message){
        this.status=status;
        this.message=message;
    }

    public HttpStatus getStatus(){
        return this.status;
    }
    public String getMessage(){
        return this.message;
    }
}
