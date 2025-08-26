package com.annyang.diagnosis.exception;

import com.annyang.global.exception.BusinessException;
import com.annyang.global.response.ErrorCode;

public class DiagnosisNotFoundException extends BusinessException {
    public DiagnosisNotFoundException() {
        super(ErrorCode.DIAGNOSIS_NOT_FOUND);
    }
}
