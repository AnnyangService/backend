package com.annyang.infrastructure.client;

import com.annyang.diagnosis.exception.DiagnosisException;
import com.annyang.infrastructure.client.dto.PostChatbotQueryToAiRequest;
import com.annyang.infrastructure.client.dto.PostChatbotQueryToAiResponse;
import com.annyang.infrastructure.client.dto.PostChatbotSessionToAiRequest;
import com.annyang.infrastructure.client.dto.PostChatbotSessionToAiResponse;
import com.annyang.infrastructure.client.dto.PostFirstStepDiagnosisToAiRequest;
import com.annyang.infrastructure.client.dto.PostFirstStepDiagnosisToAiResponse;
import com.annyang.infrastructure.client.dto.PostSecondStepDiagnosisToAiRequest;
import com.annyang.infrastructure.client.dto.PostThirdStepDiagnosisToAiRequest;
import com.annyang.infrastructure.client.dto.PostThirdStepDiagnosisToAiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.annyang.chatbot.entity.ChatbotConversation;
import com.annyang.diagnosis.dto.PostThirdStepDiagnosisRequest;
import com.annyang.diagnosis.entity.ThirdStepDiagnosis;

@Component
@RequiredArgsConstructor
public class AiServerClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${ai.server.url}")
    private String aiServerUrl;

    public PostFirstStepDiagnosisToAiResponse requestFirstDiagnosis(String imageUrl) {
        String endpoint = "/diagnosis/step1/";
        try {
            System.out.println("AI 서버로 진단 요청: " + aiServerUrl + endpoint);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            PostFirstStepDiagnosisToAiRequest request = new PostFirstStepDiagnosisToAiRequest(imageUrl);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServerUrl + endpoint, entity, String.class);
            
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            return new PostFirstStepDiagnosisToAiResponse(
                    data.path("is_normal").asBoolean(),
                    data.path("confidence").asDouble()
            );
        } catch (Exception e) {
            throw new DiagnosisException();
        }
    }

    public boolean requestSecondDiagnosis(String diagnosisId, String password, String imageUrl) {
        String endpoint = "/diagnosis/step2/";
        try {
            System.out.println("AI 서버로 2단계 진단 요청: " + aiServerUrl + endpoint);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            PostSecondStepDiagnosisToAiRequest request = new PostSecondStepDiagnosisToAiRequest(diagnosisId, password, imageUrl);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);

            restTemplate.postForEntity(
                aiServerUrl + endpoint, entity, String.class);
            
            return true;
        } catch (Exception e) {
            /**
            TODO AI 서버 구현 완료 후 mock 데이터 반환하는 대신 예외처리
            throw new DiagnosisException();
             */
            return true;
        }
    }

    public PostThirdStepDiagnosisToAiResponse requestThirdDiagnosis(String secondStepDiagnosisResult, List<PostThirdStepDiagnosisRequest.UserResponse> userResponses) {
        String endpoint = "/diagnosis/step3/";
        try {
            System.out.println("AI 서버로 3단계 진단 요청: " + aiServerUrl + endpoint);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            PostThirdStepDiagnosisToAiRequest request = new PostThirdStepDiagnosisToAiRequest(
                secondStepDiagnosisResult,
                userResponses.stream()
                    .map(userResponse -> new PostThirdStepDiagnosisToAiRequest.UserResponse(
                        Integer.parseInt(userResponse.getDiagnosisRuleId()), userResponse.getUserResponse()))
                    .toList());
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                aiServerUrl + endpoint, entity, String.class);
            
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            
            // attribute_analysis 파싱
            Map<String, PostThirdStepDiagnosisToAiResponse.AttributeAnalysis> attributeAnalysis = new HashMap<>();
            JsonNode attributeAnalysisNode = data.path("attribute_analysis");
            if (!attributeAnalysisNode.isMissingNode()) {
                attributeAnalysisNode.fields().forEachRemaining(entry -> {
                    String key = entry.getKey();
                    JsonNode value = entry.getValue();
                    String llmAnalysis = value.path("llm_analysis").asText();
                    attributeAnalysis.put(key, new PostThirdStepDiagnosisToAiResponse.AttributeAnalysis(llmAnalysis));
                });
            }
            
            return new PostThirdStepDiagnosisToAiResponse(
                data.path("category").asText(),
                data.path("summary").asText(),
                data.path("details").asText(),
                attributeAnalysis
            );
        } catch (Exception e) {
            System.out.println("AI 서버 요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new DiagnosisException();
        }
    }

    public PostChatbotSessionToAiResponse createChatbotSession(String query, ThirdStepDiagnosis thirdStepDiagnosis) {
        String endpoint = "/chat/first";
        try {
            System.out.println("AI 서버 챗봇 세션 생성 요청: " + aiServerUrl + endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            PostChatbotSessionToAiRequest request = new PostChatbotSessionToAiRequest(query, thirdStepDiagnosis);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                aiServerUrl + endpoint, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            return new PostChatbotSessionToAiResponse(
                    data.path("answer").asText(),
                    data.path("error").asText()
            );
        } catch (Exception e) {
            System.out.println("AI 서버 요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new DiagnosisException();
        }
    }

    public PostChatbotQueryToAiResponse submitChatbotQuery(String query, String diagnosisResult, List<ChatbotConversation> conversations) {
        String endpoint = "/chat/second";

        try {
            System.out.println("AI 서버 챗봇 질문 전송 요청: " + aiServerUrl + endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            PostChatbotQueryToAiRequest request = new PostChatbotQueryToAiRequest(query, diagnosisResult);
            for(int i=0; i<conversations.size(); i++) {
                ChatbotConversation conversation = conversations.get(i);
                if (i == 0) {
                    request.setPreviousQuestion(conversation.getQuestion());
                    request.setPreviousAnswer(conversation.getAnswer());
                } else if (i == 1) {
                    request.setTwoTurnQuestion(conversation.getQuestion());
                    request.setTwoTurnAnswer(conversation.getAnswer());
                }
            }
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServerUrl + endpoint, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            return objectMapper.treeToValue(data, PostChatbotQueryToAiResponse.class);
        } catch (Exception e) {
            System.out.println("AI 서버 요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new DiagnosisException();
        }
    }
}