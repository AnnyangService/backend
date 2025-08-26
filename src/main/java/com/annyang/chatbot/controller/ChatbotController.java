package com.annyang.chatbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/session")
    public ResponseEntity<ApiResponse<PostChatbotSessionResponse>> createChatbotSession(@RequestBody PostChatbotSessionRequest request) {
        PostChatbotSessionResponse response = chatbotService.createChatbotSession(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
