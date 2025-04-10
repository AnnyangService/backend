package com.annyang.auth.exception;

import com.annyang.global.exception.BusinessException;
import com.annyang.global.response.ErrorCode;

public class TokenExpiredException extends BusinessException {
    public TokenExpiredException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }
} 