package com.annyang.chatbot.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.annyang.chatbot.dto.GetChatbotSessionResponse;
import com.annyang.chatbot.dto.PostChatbotGeneralSessionRequest;
import com.annyang.chatbot.dto.PostChatbotGeneralSessionResponse;
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
import com.annyang.infrastructure.client.dto.PostChatbotGeneralQueryToAiResponse;

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

    public PostChatbotGeneralSessionResponse createGeneralChatbotSession(PostChatbotGeneralSessionRequest request) {
        PostChatbotGeneralQueryToAiResponse response = aiServerClient.submitGeneralChatbotQuery(request.getQuery(), List.of());
        ChatbotSession chatbotSession = chatbotSessionRepository.save(ChatbotSession.createGeneralChatbotSession());
        ChatbotConversation chatbotConversation = chatbotConversationRepository.save(new ChatbotConversation(chatbotSession, request.getQuery(), response.getAnswer()));
        return new PostChatbotGeneralSessionResponse(chatbotSession.getId(), chatbotConversation.getQuestion(), chatbotConversation.getAnswer());
    }

    public PostChatbotConversationResponse submitQuery(String sessionId, PostChatbotConversationRequest request) {
        ChatbotSession session = chatbotSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ChatbotSessionNotFoundException());
        List<ChatbotConversation> conversations = chatbotConversationRepository.findTop2ByChatbotSessionIdOrderByCreatedAtDesc(sessionId);
        
        ChatbotConversation conversation = createConversation(session, request.getQuery(), conversations);
        conversation = chatbotConversationRepository.save(conversation);
        return PostChatbotConversationResponse.builder()
                .answer(conversation.getAnswer())
                .build();
    }

    private ChatbotConversation createConversation(ChatbotSession session, String query, List<ChatbotConversation> recentConversations) {
        if(session.isDiagnosisBased()) {
            PostChatbotQueryToAiResponse response = aiServerClient.submitChatbotQuery(
                query, 
                session.getDiagnosisResult(),
                recentConversations
            );
            return new ChatbotConversation(session, query, response.getAnswer());
        } else {
            PostChatbotGeneralQueryToAiResponse response = aiServerClient.submitGeneralChatbotQuery(
                query, 
                recentConversations
            );
            return new ChatbotConversation(session, query, response.getAnswer());
        }
    }

    public GetChatbotSessionResponse getChatbotSession(String sessionId) {
        if (!chatbotSessionRepository.existsById(sessionId)) {
            throw new ChatbotSessionNotFoundException();
        }
        
        List<ChatbotConversation> conversations = chatbotConversationRepository
                .findByChatbotSessionIdOrderByCreatedAtAsc(sessionId);
        
        List<GetChatbotSessionResponse.ConversationDto> conversationDtos = conversations.stream()
                .map(conversation -> GetChatbotSessionResponse.ConversationDto.builder()
                        .question(conversation.getQuestion())
                        .answer(conversation.getAnswer())
                        .createdAt(conversation.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return GetChatbotSessionResponse.builder()
                .conversations(conversationDtos)
                .build();
    }
}
