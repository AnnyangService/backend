package com.annyang.diagnosis.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostSecondStepDiagnosisToAiRequest {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("password")
    private String password; // 이후 AI 서버 -> API 서버 호출할때 필요
    
    @JsonProperty("image_url")
    private String imageUrl;
}