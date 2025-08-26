package com.annyang.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostChatbotSessionToAiResponse {
    String answer;
    String error;
}
