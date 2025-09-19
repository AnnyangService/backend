package com.annyang.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostChatbotGeneralSessionResponse {
    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("first_conversation")
    private Conversation firstConversation;

    public PostChatbotGeneralSessionResponse(String sessionId, String question, String answer) {
        this.sessionId = sessionId;
        this.firstConversation = new Conversation(question, answer);
    }

    @Getter
    @AllArgsConstructor
    class Conversation {
        private String question;
        private String answer;
    }
}
