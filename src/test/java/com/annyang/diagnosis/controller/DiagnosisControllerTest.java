package com.annyang.diagnosis.controller;

import com.annyang.Main;
import com.annyang.diagnosis.dto.api.PostFirstStepDiagnosisRequest;
import com.annyang.diagnosis.dto.api.UpdateSecondStepDiagnosisRequest;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc
@Transactional
public class DiagnosisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    private PostFirstStepDiagnosisRequest diagnosisRequest;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    private static final String USER_ID = "testUser";

    private static String password;

    @BeforeEach
    void setUp() {
        // 진단 요청 객체 생성
        diagnosisRequest = new PostFirstStepDiagnosisRequest();
        diagnosisRequest.setImageUrl("https://s3.bucket/path/to/image.jpg");
        
        // 첫 번째 단계 진단 AI 서버의 응답을 모킹
        String mockFirstStepResponse = "{"
                + "\"success\": true,"
                + "\"message\": \"Success\","
                + "\"data\": {"
                + "  \"is_normal\": true,"
                + "  \"confidence\": 0.9999570846557617"
                + "}"
                + "}";
        
        ResponseEntity<String> mockFirstStepResponseEntity = 
                new ResponseEntity<>(mockFirstStepResponse, HttpStatus.OK);
        
        // 첫 번째 단계 진단 RestTemplate 모킹
        when(restTemplate.postForEntity(
                eq(aiServerUrl + "/diagnosis/step1/"),
                any(),
                eq(String.class)))
                .thenReturn(mockFirstStepResponseEntity);
        
        // 두 번째 단계 진단 요청 RestTemplate 모킹
        when(restTemplate.postForEntity(
                eq(aiServerUrl + "/diagnosis/step2/"), // URL from selection
                any(),
                eq(String.class)))
                .thenAnswer(invocation -> {
                        Object[] args = invocation.getArguments();
                        Object requestObject = args[1]; // This is the actual request object passed to postForEntity
                        String requestObjectString = objectMapper.writeValueAsString(requestObject);
                        System.out.println("요청 본문: " + requestObjectString);

                        String requestBodyString = objectMapper.readTree(requestObjectString).path("body").asText();
                        JsonNode requestData = objectMapper.readTree(requestBodyString);
                        // JsonNode로 변환하여 "password" 필드를 추출합니다.
                        String extractedPassword = requestData.path("password").asText();
                        System.out.println("비밀번호: " + extractedPassword);
                        System.out.println("진단 ID: " + requestData.path("id").asText());
                        
                        DiagnosisControllerTest.password = extractedPassword;
                        
                        return new ResponseEntity<>("{\"success\":true}", HttpStatus.OK);
                });
    }

    @Test
    @DisplayName("진단 첫 단계 API 성공")
    @WithMockUser(username = USER_ID)
    void diagnosisFirstStep_Success() throws Exception {
        mockMvc.perform(post("/diagnosis/step1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(diagnosisRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.is_normal").value(true))
                .andExpect(jsonPath("$.data.confidence").value(0.9999570846557617))
                .andReturn();
    }

    @Test
    @DisplayName("이미지 URL이 비어있는 경우 실패")
    @WithMockUser(username = USER_ID)
    void diagnosisFirstStep_EmptyImageURL() throws Exception {
        diagnosisRequest.setImageUrl("");
        
        mockMvc.perform(post("/diagnosis/step1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(diagnosisRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("G002")); // INVALID_INPUT 에러 코드
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 진단을 요청할 수 없다")
    void diagnosisFirstStep_Unauthorized() throws Exception {
        mockMvc.perform(post("/diagnosis/step1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(diagnosisRequest)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("1단계 요청 후 2단계 진단 요청 성공")
    @WithMockUser(username = USER_ID)
    void getDiagnosisSecondStep_Success() throws Exception {
        // 1단계 요청
        MvcResult resultStep1 = mockMvc.perform(post("/diagnosis/step1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(diagnosisRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
        
        String contentStep1 = resultStep1.getResponse().getContentAsString();
        
        String diagnosisId = objectMapper.readTree(contentStep1)
                .path("data").path("id").asText();

        // 2단계 요청
        mockMvc.perform(get("/diagnosis/step2/" + diagnosisId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty())
                .andReturn();
    }
    
    @Test
    @DisplayName("진단 두 번째 단계 업데이트 API 성공")
    @WithMockUser(username = USER_ID)
    void updateDiagnosisSecondStep_Success() throws Exception {
        // Given
        MvcResult resultStep1 = mockMvc.perform(post("/diagnosis/step1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(diagnosisRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
        String contentStep1 = resultStep1.getResponse().getContentAsString();
        String diagnosisId = objectMapper.readTree(contentStep1)
                .path("data").path("id").asText();

        // When
        UpdateSecondStepDiagnosisRequest updateSecondDiagnosisRequest = UpdateSecondStepDiagnosisRequest.builder()
            .id(diagnosisId)
            .category("testCategory")
            .confidence(0.95)
            .password(password)
            .build();

        // Then
        mockMvc.perform(put("/diagnosis/step2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateSecondDiagnosisRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(true)); // 성공 시 데이터는 비어있음

        // 진단 결과 조회
        mockMvc.perform(get("/diagnosis/step2/" + diagnosisId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.category").value(updateSecondDiagnosisRequest.getCategory()))
            .andExpect(jsonPath("$.data.confidence").value(updateSecondDiagnosisRequest.getConfidence()))
            .andExpect(jsonPath("$.data.id").value(diagnosisId));
    }
    
    @Test
    @DisplayName("진단 두 번째 단계 업데이트 API 실패 - 잘못된 패스워드")
    @WithMockUser(username = USER_ID)
    void updateDiagnosisSecondStep_InvalidPassword() throws Exception {
        // Given
        MvcResult resultStep1 = mockMvc.perform(post("/diagnosis/step1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(diagnosisRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
        String contentStep1 = resultStep1.getResponse().getContentAsString();
        String diagnosisId = objectMapper.readTree(contentStep1)
                .path("data").path("id").asText();

        // When
        UpdateSecondStepDiagnosisRequest updateSecondDiagnosisRequest = UpdateSecondStepDiagnosisRequest.builder()
            .id(diagnosisId)
            .category("testCategory")
            .confidence(0.95)
            .password("wrong" + password) // 잘못된 패스워드
            .build();
        
        // Then
        mockMvc.perform(put("/diagnosis/step2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateSecondDiagnosisRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("A001")); // UNAUTHORIZED 에러 코드
    }
}
