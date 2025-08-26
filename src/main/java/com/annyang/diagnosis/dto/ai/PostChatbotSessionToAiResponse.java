package com.annyang.diagnosis.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostChatbotSessionToAiResponse {
    String answer;
    String error;
}
