package com.annyang.diagnosis.dto.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetDiagnosisRuleResponse {
    private List<DiagnosisRule> rules;

    @Builder
    public GetDiagnosisRuleResponse(List<com.annyang.diagnosis.entity.DiagnosisRule> rules) {
        this.rules = rules.stream()
                .map(rule -> new DiagnosisRule(rule.getId(), rule.getName()))
                .toList();
    }

    @Getter
    @AllArgsConstructor
    public static class DiagnosisRule {
        private String id;
        private String name;
    }
}


