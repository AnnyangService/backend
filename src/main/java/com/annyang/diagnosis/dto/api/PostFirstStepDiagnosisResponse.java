package com.annyang.diagnosis.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostFirstStepDiagnosisResponse {
    
    private String id;
    @JsonProperty("is_normal")
    private boolean normal; // isNormal에서 normal로 변경
    private double confidence;
}
