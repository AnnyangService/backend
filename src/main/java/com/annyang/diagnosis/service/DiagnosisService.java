package com.annyang.diagnosis.service;

import com.annyang.diagnosis.dto.DiagnosisRequest;
import com.annyang.diagnosis.dto.DiagnosisResponse;

import org.springframework.stereotype.Service;

@Service
public class DiagnosisService {
    
    public DiagnosisResponse diagnoseFirstStep(DiagnosisRequest request) {
        // 단순한 상수 반환 구현
        return DiagnosisResponse.builder()
                .id("01JTTKJYG28CFYMBKXC0Q80F61")
                .isNormal(false)
                .confidence(0.90)
                .build();
    }
}