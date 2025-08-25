package com.annyang.diagnosis.dto.api;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import com.annyang.diagnosis.entity.ThirdStepDiagnosis;

@Getter
public class PostThirdStepDiagnosisResponse {
    private String id;
    private String category;
    private String summary;
    private String details;
    private Map<String, AttributeAnalysis> attributeAnalysis;
    
    @Getter
    @Builder
    public static class AttributeAnalysis {
        private String llmAnalysis;
    }

    public PostThirdStepDiagnosisResponse(ThirdStepDiagnosis thirdStepDiagnosis) {
        this.id = thirdStepDiagnosis.getId();
        this.category = thirdStepDiagnosis.getCategory();
        this.summary = thirdStepDiagnosis.getSummary();
        this.details = thirdStepDiagnosis.getDetails();
        this.attributeAnalysis = new HashMap<>();
        thirdStepDiagnosis.getAttributeAnalysis().forEach((key, value) -> {
            this.attributeAnalysis.put(key, new AttributeAnalysis(value.getLlmAnalysis()));
        });
    }
}
