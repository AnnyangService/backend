package com.annyang.chatbot.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetChatbotSetssionListResponse {
    private List<SessionDto> sessions;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionDto {
        @JsonProperty("session_id")
        private String sessionId;
        
        @JsonProperty("is_diagnosis_based")
        private boolean isDiagnosisBased;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;
    }
}
