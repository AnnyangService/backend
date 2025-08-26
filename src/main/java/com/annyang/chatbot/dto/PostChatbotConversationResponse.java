package com.annyang.chatbot.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostChatbotConversationResponse {
    private String answer;
}
