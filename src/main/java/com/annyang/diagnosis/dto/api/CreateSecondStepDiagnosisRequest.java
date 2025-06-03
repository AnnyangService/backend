package com.annyang.diagnosis.dto.api;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateSecondStepDiagnosisRequest {
    private String id;
    private String password;
    private String category;
    private double confidence;
}