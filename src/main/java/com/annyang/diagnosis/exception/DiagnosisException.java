package com.annyang.diagnosis.exception;

import com.annyang.global.exception.BusinessException;
import com.annyang.global.response.ErrorCode;

public class DiagnosisException extends BusinessException {
    public DiagnosisException() {
        super(ErrorCode.DIAGNOSIS_SERVER_ERROR);
    }
}
