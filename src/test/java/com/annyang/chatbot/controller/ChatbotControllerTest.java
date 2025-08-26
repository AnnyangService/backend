package com.annyang.chatbot.controller;

import com.annyang.Main;
import com.annyang.chatbot.dto.PostChatbotSessionRequest;
import com.annyang.diagnosis.entity.FirstStepDiagnosis;
import com.annyang.diagnosis.entity.SecondStepDiagnosis;
import com.annyang.diagnosis.entity.ThirdStepDiagnosis;
import com.annyang.diagnosis.repository.FirstStepDiagnosisRepository;
import com.annyang.diagnosis.repository.SecondStepDiagnosisRepository;
import com.annyang.diagnosis.repository.ThirdStepDiagnosisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc
@Transactional
public class ChatbotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private FirstStepDiagnosisRepository firstStepDiagnosisRepository;

    @Autowired
    private SecondStepDiagnosisRepository secondStepDiagnosisRepository;

    @Autowired
    private ThirdStepDiagnosisRepository thirdStepDiagnosisRepository;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    private static final String USER_ID = "testUser";
    private ThirdStepDiagnosis savedThirdStepDiagnosis;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        FirstStepDiagnosis firstStepDiagnosis = FirstStepDiagnosis.builder()
                .imageUrl("https://s3.bucket/path/to/image.jpg")
                .isNormal(false)
                .confidence(0.95)
                .passwordForSecondStep("password123")
                .build();
        firstStepDiagnosis = firstStepDiagnosisRepository.save(firstStepDiagnosis);

        SecondStepDiagnosis secondStepDiagnosis = new SecondStepDiagnosis(
                firstStepDiagnosis, "password123", "비궤양성 각막염", 0.88);
        secondStepDiagnosis = secondStepDiagnosisRepository.save(secondStepDiagnosis);

        Map<String, ThirdStepDiagnosis.AttributeAnalysis> attributeAnalysis = new HashMap<>();
        attributeAnalysis.put("분비물 특성", new ThirdStepDiagnosis.AttributeAnalysis("점액성 분비물이 관찰됩니다."));
        attributeAnalysis.put("진행 속도", new ThirdStepDiagnosis.AttributeAnalysis("급성으로 진행되고 있습니다."));
        savedThirdStepDiagnosis = ThirdStepDiagnosis.builder()
                .firstStepDiagnosis(firstStepDiagnosis)
                .category("비궤양성 각막염")
                .summary("급성 비궤양성 각막염으로 진단됩니다.")
                .details("점액성 분비물과 함께 급성으로 진행되는 각막 염증이 관찰됩니다. 적절한 항염 치료가 필요합니다.")
                .attributeAnalysis(attributeAnalysis)
                .build();
        thirdStepDiagnosisRepository.save(savedThirdStepDiagnosis);

        // AI 서버 첫 번째 챗봇 세션 생성 응답 모킹
        String mockChatbotSessionResponse = """
            {
                "success": true,
                "message": "Success",
                "data": {
                    "answer": "안녕하세요! 비궤양성 각막염 진단 결과에 대해 궁금한 점이 있으시군요. 어떤 것이 궁금하신지 알려주세요.",
                    "error": ""
                }
            }
            """;

        ResponseEntity<String> mockChatbotSessionResponseEntity = 
                new ResponseEntity<>(mockChatbotSessionResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq(aiServerUrl + "/chat/first"),
                any(),
                eq(String.class)))
                .thenReturn(mockChatbotSessionResponseEntity);

        // AI 서버 챗봇 질문 응답 모킹
        String mockChatbotQueryResponse = """
            {
                "success": true,
                "message": "Success",
                "data": {
                    "answer": "비궤양성 각막염은 각막 표면에 궤양 없이 발생하는 염증성 질환입니다. 면역 매개성인 경우가 많으며, 적절한 치료를 받으면 증상 완화가 가능합니다.",
                    "error": "",
                    "retrieved_documents": [
                        {
                            "chunk": {
                                "id": 16,
                                "content": "각막 표면의 손상이나 궤양 없이 발생하는 각막의 염증으로, 면역 매개성 질환인 경우가 많습니다.",
                                "keywords": ["면역 매개성", "궤양"],
                                "source": "비궤양성각막염.정의"
                            },
                            "similarity": 0.8180955359392269,
                            "chunk_id": "16"
                        }
                    ]
                }
            }
            """;

        ResponseEntity<String> mockChatbotQueryResponseEntity = 
                new ResponseEntity<>(mockChatbotQueryResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq(aiServerUrl + "/chat/second"),
                any(),
                eq(String.class)))
                .thenReturn(mockChatbotQueryResponseEntity);
    }

    @Test
    @DisplayName("챗봇 세션 생성 API 성공")
    @WithMockUser(username = USER_ID)
    void createChatbotSession_Success() throws Exception {
        // Given
        PostChatbotSessionRequest request = PostChatbotSessionRequest.builder()
                .diagnosisId(savedThirdStepDiagnosis.getId())
                .query("이 질병에 대해 알려주세요.")
                .build();

        // When & Then
        mockMvc.perform(post("/chatbot/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.session_id").exists())
                .andExpect(jsonPath("$.data.first_conversation.question").exists())
                .andExpect(jsonPath("$.data.first_conversation.answer").exists());
    }
}
