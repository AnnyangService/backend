package com.annyang.diagnosis.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PostThirdStepDiagnosisToAiRequest {
    private String secondStepDiagnosisResult;
    private List<UserResponse> attributes;

    @AllArgsConstructor
    @Getter
    public static class UserResponse {
        public Integer id;
        public String description;
    }
}


