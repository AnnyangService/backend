package com.annyang.diagnosis.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostThirdStepDiagnosisToAiResponse {
    private String category;
    private String description;
}
