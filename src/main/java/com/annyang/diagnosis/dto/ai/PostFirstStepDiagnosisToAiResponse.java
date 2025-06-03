package com.annyang.diagnosis.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public  class PostFirstStepDiagnosisToAiResponse {
    @JsonProperty("is_normal")
    private boolean isNormal;
    
    private double confidence;
}
