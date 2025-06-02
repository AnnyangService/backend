package com.annyang.diagnosis.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class DiagnosisDto {

    @Getter
    @AllArgsConstructor
    public static class FirstStepRequest {
        @JsonProperty("image_url")
        private String image_url;
    }

    @Getter
    @AllArgsConstructor
    public static class FirstStepResponse {
        @JsonProperty("is_normal")
        private boolean isNormal;
        
        private double confidence;
    }
}