package com.annyang.global.exception;

import com.annyang.global.response.ErrorCode;

public abstract class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    
    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
} 