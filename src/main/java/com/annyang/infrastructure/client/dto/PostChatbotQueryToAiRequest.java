package com.annyang.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PostChatbotQueryToAiRequest {
    @NonNull
    String query;
    
    @NonNull
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
