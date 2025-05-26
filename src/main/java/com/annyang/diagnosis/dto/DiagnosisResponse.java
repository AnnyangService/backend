package com.annyang.diagnosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiagnosisResponse {
    
    private String id;
    @JsonProperty("is_normal")
    private boolean isNormal;
    private double confidence;
}
