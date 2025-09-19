package com.annyang.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostChatbotGeneralQueryToAiResponse {
    String answer;
    String error;
}
