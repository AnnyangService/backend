package com.annyang.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostChatbotSessionRequest {
    
    @JsonProperty("diagnosis_id")
    private String diagnosisId;

    private String query;
}
