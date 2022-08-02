package com.example.monster.common.authenticatior;

public enum ErrorCode {
    NOACCESS_TOKEN("E000","no access token -> 토큰이 없습니다."),
    UNSUPPORTED_TOKEN("E001", "unsupported token -> 지원하지 않는 토큰입니다."),
    EXPIRED_TOKEN("E002", "token expired -> 토큰 유효기간이 만료되었습니다."),
    ILLEGALARGU_TOKEN("E003", "illegal argument token ->  적절하지 않은 토큰입니다.");

    private final String code;
    private final String message;

    ErrorCode (String code, String message){
        this.code=code;
        this.message=message;
    }

    public String getCode(){
        return this.code;
    }
    public String getMessage(){
        return this.message;
    }
}
