package com.annyang.chatbot.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetChatbotSessionResponse {
    private List<ConversationDto> conversations;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationDto {
        private String question;
        private String answer;
        private LocalDateTime createdAt;
    }
}
