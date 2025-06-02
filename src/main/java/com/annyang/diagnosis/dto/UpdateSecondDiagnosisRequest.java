package com.annyang.diagnosis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateSecondDiagnosisRequest {
    private String id;
    private String password;
    private String category;
    private double confidence;
}