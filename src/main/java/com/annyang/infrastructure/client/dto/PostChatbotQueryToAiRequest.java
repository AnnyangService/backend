package com.annyang.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostChatbotQueryToAiRequest {
    String query;
    
    @JsonProperty("diagnosis_result")
    String diagnosisResult;
    
    @JsonProperty("previous_question")
    String previousQuestion;
    
    @JsonProperty("previous_answer")
    String previousAnswer;
    
    @JsonProperty("two_turn_question")
    String twoTurnQuestion;
    
    @JsonProperty("two_turn_answer")
    String twoTurnAnswer;
}
