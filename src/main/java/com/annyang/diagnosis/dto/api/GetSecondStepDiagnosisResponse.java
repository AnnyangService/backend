package com.annyang.diagnosis.dto.api;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetSecondStepDiagnosisResponse {
    private String id;
    private String category;
    private double confidence;
}