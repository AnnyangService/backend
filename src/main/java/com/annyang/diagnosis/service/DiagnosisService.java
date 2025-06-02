package com.annyang.diagnosis.service;

import com.annyang.diagnosis.client.AiServerClient;
import com.annyang.diagnosis.client.DiagnosisDto.FirstStepResponse;
import com.annyang.diagnosis.dto.DiagnosisRequest;
import com.annyang.diagnosis.dto.DiagnosisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagnosisService {
    
    private final AiServerClient aiServerClient;
    
    public DiagnosisResponse diagnoseFirstStep(DiagnosisRequest request) {
        FirstStepResponse response = aiServerClient.requestFirstDiagnosis(request.getImageUrl());
        return DiagnosisResponse.builder()
                .id(UUID.randomUUID().toString().replace("-", "").substring(0, 30))
                .normal(response.isNormal())
                .confidence(response.getConfidence())
                .build();
    }
}