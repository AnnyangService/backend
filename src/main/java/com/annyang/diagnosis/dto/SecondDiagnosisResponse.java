package com.annyang.diagnosis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SecondDiagnosisResponse {
    private String id;
    private String category;
    private double confidence;
}