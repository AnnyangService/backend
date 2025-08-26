package com.annyang.chatbot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.annyang.chatbot.dto.PostChatbotConversationRequest;
import com.annyang.chatbot.dto.PostChatbotConversationResponse;
import com.annyang.chatbot.dto.PostChatbotSessionRequest;
import com.annyang.chatbot.dto.PostChatbotSessionResponse;
import com.annyang.chatbot.entity.ChatbotConversation;
import com.annyang.chatbot.entity.ChatbotSession;
import com.annyang.chatbot.exception.ChatbotSessionNotFoundException;
import com.annyang.chatbot.repository.ChatbotConversationRepository;
import com.annyang.chatbot.repository.ChatbotSessionRepository;
import com.annyang.diagnosis.entity.ThirdStepDiagnosis;
import com.annyang.diagnosis.exception.DiagnosisNotFoundException;
import com.annyang.diagnosis.repository.ThirdStepDiagnosisRepository;
import com.annyang.infrastructure.client.AiServerClient;
import com.annyang.infrastructure.client.dto.PostChatbotQueryToAiResponse;
import com.annyang.infrastructure.client.dto.PostChatbotSessionToAiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final AiServerClient aiServerClient;
    private final ThirdStepDiagnosisRepository thirdStepDiagnosisRepository;
    private final ChatbotSessionRepository chatbotSessionRepository;
    private final ChatbotConversationRepository chatbotConversationRepository;

    public PostChatbotSessionResponse createChatbotSession(PostChatbotSessionRequest request) {
        ThirdStepDiagnosis diagnosis = thirdStepDiagnosisRepository.findById(request.getDiagnosisId())
                .orElseThrow(() -> new DiagnosisNotFoundException());
        PostChatbotSessionToAiResponse response = aiServerClient.createChatbotSession(request.getQuery(), diagnosis);
        ChatbotSession chatbotSession = chatbotSessionRepository.save(new ChatbotSession(diagnosis));
        ChatbotConversation chatbotConversation = chatbotConversationRepository.save(new ChatbotConversation(chatbotSession, request.getQuery(), response.getAnswer()));
        return new PostChatbotSessionResponse(chatbotSession.getId(), chatbotConversation.getQuestion(), chatbotConversation.getAnswer());
    }

    public PostChatbotConversationResponse submitQuery(String sessionId, PostChatbotConversationRequest request) {
        ChatbotSession session = chatbotSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ChatbotSessionNotFoundException());
        List<ChatbotConversation> conversations = chatbotConversationRepository.findTop2ByChatbotSessionIdOrderByCreatedAtDesc(sessionId);
        PostChatbotQueryToAiResponse response = aiServerClient.submitChatbotQuery(
            request.getQuery(), 
            session.getDiagnosisResult(),
            conversations
        );
        ChatbotConversation conversation = new ChatbotConversation(session, request.getQuery(), response.getAnswer());
        chatbotConversationRepository.save(conversation);
        return PostChatbotConversationResponse.builder()
                .answer(conversation.getAnswer())
                .build();
    }
}
