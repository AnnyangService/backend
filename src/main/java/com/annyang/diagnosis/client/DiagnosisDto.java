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
    
    @Getter
    @AllArgsConstructor
    public static class SecondStepRequest {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("password")
        private String password; // 이후 AI 서버 -> API 서버 호출할때 필요
        
        @JsonProperty("image_url")
        private String imageUrl;
    }
    
    @Getter
    @AllArgsConstructor
    public static class SecondStepResponse {
        private String category;
        
        private double confidence;
    }
}