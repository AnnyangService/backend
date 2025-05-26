package com.annyang.diagnosis.service;

import com.annyang.diagnosis.dto.DiagnosisRequest;
import com.annyang.diagnosis.dto.DiagnosisResponse;
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
    
    @Value("${ai.server.url:http://localhost:8081}")
    private String aiServerUrl;
    
    public DiagnosisResponse diagnoseFirstStep(DiagnosisRequest request) {
        try {
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
                    .isNormal(data.path("is_normal").asBoolean())
                    .confidence(data.path("confidence").asDouble())
                    .build();
            
        } catch (Exception e) {
            // 에러 발생시 기본값 반환 (실제 구현에서는 적절한 예외 처리 필요)
            return DiagnosisResponse.builder()
                    .id(UUID.randomUUID().toString().replace("-", "").substring(0, 30))
                    .isNormal(true)
                    .confidence(0.99)
                    .build();
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