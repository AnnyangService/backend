package com.annyang.auth.exception;

import com.annyang.global.exception.BusinessException;
import com.annyang.global.response.ErrorCode;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
} 