package com.annyang.chatbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annyang.chatbot.dto.GetChatbotSessionResponse;
import com.annyang.chatbot.dto.PostChatbotConversationRequest;
import com.annyang.chatbot.dto.PostChatbotConversationResponse;
import com.annyang.chatbot.dto.PostChatbotGeneralSessionRequest;
import com.annyang.chatbot.dto.PostChatbotGeneralSessionResponse;
import com.annyang.chatbot.dto.PostChatbotSessionRequest;
import com.annyang.chatbot.dto.PostChatbotSessionResponse;
import com.annyang.chatbot.service.ChatbotService;
import com.annyang.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/sessions/diagnosis")
    public ResponseEntity<ApiResponse<PostChatbotSessionResponse>> createChatbotSession(@RequestBody PostChatbotSessionRequest request) {
        PostChatbotSessionResponse response = chatbotService.createChatbotSession(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/sessions/general")
    public ResponseEntity<ApiResponse<PostChatbotGeneralSessionResponse>> createGeneralChatbotSession(@RequestBody PostChatbotGeneralSessionRequest request) {
        PostChatbotGeneralSessionResponse response = chatbotService.createGeneralChatbotSession(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/sessions/{sessionId}/conversations")
    public ResponseEntity<ApiResponse<PostChatbotConversationResponse>> submitQuery(
            @PathVariable String sessionId,
            @RequestBody PostChatbotConversationRequest request) {
        PostChatbotConversationResponse response = chatbotService.submitQuery(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/sessions/{sessionId}/conversations")
    public ResponseEntity<ApiResponse<GetChatbotSessionResponse>> getChatbotSession(@PathVariable String sessionId) {
        GetChatbotSessionResponse response = chatbotService.getChatbotSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
