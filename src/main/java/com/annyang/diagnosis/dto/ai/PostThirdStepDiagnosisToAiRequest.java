package com.annyang.diagnosis.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PostThirdStepDiagnosisToAiRequest {
    private List<UserResponse> userResponses;

    @AllArgsConstructor
    @Getter
    public static class UserResponse {
        public String diagnosisRuleId;
        public String userResponse;
    }
}


