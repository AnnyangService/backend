package com.annyang.diagnosis.client;

import com.annyang.diagnosis.exception.DiagnosisException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.annyang.diagnosis.dto.ai.PostFirstStepDiagnosisToAiRequest;
import com.annyang.diagnosis.dto.ai.PostFirstStepDiagnosisToAiResponse;
import com.annyang.diagnosis.dto.ai.PostSecondStepDiagnosisToAiRequest;

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
            // TODO AI 서버 구현 완료 후 제거
            return true;
            // throw new DiagnosisException();
        }
    }
}