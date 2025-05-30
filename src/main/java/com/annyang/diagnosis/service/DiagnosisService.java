package com.annyang.diagnosis.service;

import com.annyang.diagnosis.dto.DiagnosisRequest;
import com.annyang.diagnosis.dto.DiagnosisResponse;
import com.annyang.diagnosis.exception.DiagnosisException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagnosisService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${ai.server.url}")
    private String aiServerUrl;
    
    public DiagnosisResponse diagnoseFirstStep(DiagnosisRequest request) {
        try {
            System.out.println("AI 서버로 진단 요청: " + aiServerUrl + "/diagnosis/step1/");
            // AI 서버로 요청 준비
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // request의 imageUrl을 image_url로 변환
            AIDiagnosisRequest aiRequest = new AIDiagnosisRequest();
            aiRequest.setImage_url(request.getImageUrl());
            
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(aiRequest), headers);
            
            // AI 서버로 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServerUrl + "/diagnosis/step1/", entity, String.class);
            
            // 응답 처리
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");

            return DiagnosisResponse.builder()
                    .id(UUID.randomUUID().toString().replace("-", "").substring(0, 30))
                    .normal(data.path("is_normal").asBoolean())
                    .confidence(data.path("confidence").asDouble())
                    .build();
            
        } catch (Exception e) {
            // 비즈니스 예외로 처리하여 일관된 에러 응답 제공
            throw new DiagnosisException();
        }
    }
    
    @Getter
    @NoArgsConstructor
    static class AIDiagnosisRequest {
        @JsonProperty("image_url")
        private String image_url;
        
        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }
    }
}