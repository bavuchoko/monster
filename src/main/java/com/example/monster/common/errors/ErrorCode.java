package com.example.monster.common.errors;

import lombok.Getter;

@Getter
public enum ErrorCode {


    // Common
    INVALID_PARAMETER(400, null, "Invalid Request Data : 잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(405, "C002", " Invalid Input Value"),
    HANDLE_ACCESS_DENIED(403, "C006", "Access is Denied"),
    USERNAME_NOT_EXIST(400, "M001", "Username does not exist : 해당 아이디가 존재하지 않습니다"),
    EMAIL_DUPLICATION(400, "M001", "Duplicated username : 이미 동일한 아이디가 존재합니다."),

    // Member
    PASSWORD_NOT_MATCHED(400, "M002", "Password wrong : 비밀번호가 잘못 입력되었습니다."),

            ;
    private final String code;
    private final String message;
    private int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }


}
