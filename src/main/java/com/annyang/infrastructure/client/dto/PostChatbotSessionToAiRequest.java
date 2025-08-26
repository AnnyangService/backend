package com.annyang.infrastructure.client.dto;

import com.annyang.diagnosis.entity.ThirdStepDiagnosis;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class PostChatbotSessionToAiRequest {
    String query;
    @JsonProperty("diagnosis_result")
    String diagnosisResult;
    String summary;
    String details;

    public PostChatbotSessionToAiRequest(String query, ThirdStepDiagnosis thirdStepDiagnosis) {
        this.query = query;
        this.diagnosisResult = thirdStepDiagnosis.getCategory();
        this.summary = thirdStepDiagnosis.getSummary();
        this.details = thirdStepDiagnosis.getDetails();
    }
}
