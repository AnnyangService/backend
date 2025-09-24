package com.annyang.diagnosis.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.annyang.diagnosis.entity.ThirdStepDiagnosis;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMyDiagnosesResponse {
    private List<DiagnosisDto> diagnoses;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiagnosisDto {
        private String id;
        
        @JsonProperty("image_url")
        private String imageUrl;
        
        @JsonProperty("is_normal")
        private boolean isNormal;
        
        private double confidence;
        
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        
        // Second Step 정보
        @JsonProperty("second_step")
        private SecondStepDto secondStep;
        
        // Third Step 정보
        @JsonProperty("third_step")
        private ThirdStepDto thirdStep;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecondStepDto {
        private String category;
        private double confidence;
        
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThirdStepDto {
        private String category;
        private String summary;
        private String details;
        
        @JsonProperty("attribute_analysis")
        private Map<String, ThirdStepDiagnosis.AttributeAnalysis> attributeAnalysis;
        
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
    }
}