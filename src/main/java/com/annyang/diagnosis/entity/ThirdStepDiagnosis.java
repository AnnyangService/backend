package com.annyang.diagnosis.entity;

import com.annyang.diagnosis.converter.AttributeAnalysisMapConverter;
import com.annyang.diagnosis.dto.ai.PostThirdStepDiagnosisToAiResponse;
import com.annyang.global.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "third_step_diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThirdStepDiagnosis extends BaseEntity {

    @Id
    private String id; // PK이자 FK

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private FirstStepDiagnosis firstStepDiagnosis;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String details;

    
    @Column(columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = AttributeAnalysisMapConverter.class)
    private Map<String, AttributeAnalysis> attributeAnalysis;

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AttributeAnalysis {
        @JsonProperty("llmAnalysis")
        private String llmAnalysis;

        @JsonCreator
        public AttributeAnalysis(@JsonProperty("llmAnalysis") String llmAnalysis) {
            this.llmAnalysis = llmAnalysis;
        }
    }

    @Builder
    public ThirdStepDiagnosis(FirstStepDiagnosis firstStepDiagnosis, String category, String summary, String details, Map<String, AttributeAnalysis> attributeAnalysis) {
        this.firstStepDiagnosis = firstStepDiagnosis;
        this.category = category;
        this.summary = summary;
        this.details = details;
        this.attributeAnalysis = attributeAnalysis;
    }

    public ThirdStepDiagnosis(FirstStepDiagnosis firstStepDiagnosis, PostThirdStepDiagnosisToAiResponse response) {
        this.firstStepDiagnosis = firstStepDiagnosis;
        this.category = response.getCategory();
        this.summary = response.getSummary();
        this.details = response.getDetails();
        this.attributeAnalysis = new HashMap<>();
        response.getAttributeAnalysis().forEach((key, value) -> {
            this.attributeAnalysis.put(key, new AttributeAnalysis(value.getLlmAnalysis()));
        });
    }
}
