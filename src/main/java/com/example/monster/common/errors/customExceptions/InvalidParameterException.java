package com.example.monster.common.errors.customExceptions;

import com.example.monster.common.errors.ErrorCode;
import org.springframework.validation.Errors;

public class InvalidParameterException extends CustomException {

    private static final long serialVersionUID = -2116671122895194101L;

    private final Errors errors;

    public InvalidParameterException(Errors errors) {
        super(ErrorCode.INVALID_PARAMETER);
        this.errors = errors;
    }

    public Errors getErrors() {
        return this.errors;
    }

}