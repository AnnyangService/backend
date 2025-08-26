package com.annyang.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class PostThirdStepDiagnosisToAiResponse {
    private String category;
    private String summary;
    private String details;
    private Map<String, AttributeAnalysis> attributeAnalysis;
    
    @Getter
    @AllArgsConstructor
    public static class AttributeAnalysis {
        private String llmAnalysis;
    }
}
