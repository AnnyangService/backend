package com.annyang.chatbot.service;

import org.springframework.stereotype.Service;

import com.annyang.chatbot.dto.PostChatbotSessionRequest;
import com.annyang.chatbot.dto.PostChatbotSessionResponse;
import com.annyang.diagnosis.entity.ThirdStepDiagnosis;
import com.annyang.diagnosis.exception.DiagnosisNotFoundException;
import com.annyang.diagnosis.repository.ThirdStepDiagnosisRepository;
import com.annyang.infrastructure.client.AiServerClient;
import com.annyang.infrastructure.client.dto.PostChatbotSessionToAiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final AiServerClient aiServerClient;
    private final ThirdStepDiagnosisRepository thirdStepDiagnosisRepository;

    public PostChatbotSessionResponse createChatbotSession(PostChatbotSessionRequest request) {
        ThirdStepDiagnosis diagnosis = thirdStepDiagnosisRepository.findById(request.getDiagnosisId())
                .orElseThrow(() -> new DiagnosisNotFoundException());
        PostChatbotSessionToAiResponse response = aiServerClient.createChatbotSession(request.getQuery(), diagnosis);
        // TODO Implement the logic to create a chatbot session
        return new PostChatbotSessionResponse("sessionId", request.getQuery(), response.getAnswer());
    }
}
