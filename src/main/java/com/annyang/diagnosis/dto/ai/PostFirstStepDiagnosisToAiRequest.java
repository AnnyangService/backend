package com.annyang.diagnosis.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
    @AllArgsConstructor
public class PostFirstStepDiagnosisToAiRequest {
        @JsonProperty("image_url")
        private String image_url;
}
