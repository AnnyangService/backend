package com.annyang.cat.exception;

import com.annyang.global.exception.BusinessException;
import com.annyang.global.response.ErrorCode;

public class CatNotFoundException extends BusinessException {
    public CatNotFoundException() {
        super(ErrorCode.CAT_NOT_FOUND);
    }
} 