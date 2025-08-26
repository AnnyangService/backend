package com.annyang.chatbot.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostChatbotSessionRequest {
    private String diagnosisId;
    private String query;
}
