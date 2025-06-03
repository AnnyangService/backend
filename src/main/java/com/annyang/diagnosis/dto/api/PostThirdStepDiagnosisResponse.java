package com.annyang.diagnosis.dto.api;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostThirdStepDiagnosisResponse {
    private String id;
    private String category;
    private double confidence;
}
