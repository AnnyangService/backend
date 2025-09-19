package com.annyang.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PostChatbotGeneralQueryToAiRequest {
    @NonNull
    String query;
    
    @JsonProperty("previous_question")
    String previousQuestion;
    
    @JsonProperty("previous_answer")
    String previousAnswer;
    
    @JsonProperty("two_turn_question")
    String twoTurnQuestion;
    
    @JsonProperty("two_turn_answer")
    String twoTurnAnswer;

    public PostChatbotGeneralQueryToAiRequest(@NonNull String query) {
        this.query = query;
        this.previousQuestion = "";
        this.previousAnswer = "";
        this.twoTurnQuestion = "";
        this.twoTurnAnswer = "";
    }
}
