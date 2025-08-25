package com.annyang.diagnosis.controller;

import com.annyang.Main;
import com.annyang.diagnosis.dto.api.PostSecondStepDiagnosisRequest;
import com.annyang.diagnosis.dto.api.PostThirdStepDiagnosisRequest;
import com.annyang.diagnosis.entity.FirstStepDiagnosis;
import com.annyang.diagnosis.entity.SecondStepDiagnosis;
import com.annyang.diagnosis.repository.FirstStepDiagnosisRepository;
import com.annyang.diagnosis.repository.SecondStepDiagnosisRepository;
import com.annyang.diagnosis.repository.ThirdStepDiagnosisRepository;
import com.annyang.diagnosis.dto.api.PostFirstStepDiagnosisRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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

    @Autowired
    private FirstStepDiagnosisRepository firstStepDiagnosisRepository;

    @Autowired
    private SecondStepDiagnosisRepository secondStepDiagnosisRepository;

    @Autowired
    private ThirdStepDiagnosisRepository thirdStepDiagnosisRepository;

    @Autowired
    private EntityManager entityManager;

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

        // 세 번째 단계 진단 AI 서버의 응답을 모킹
        String mockThirdStepResponse = "{"
                + "\"success\": true,"
                + "\"message\": \"Success\","
                + "\"data\": {"
                + "  \"category\": \"결막염\","
                + "  \"summary\": \"🔍 진단 결과: 결막염\\n• 분비물 특성: 결막염 (85.2% 유사)\\n• 진행 속도: 결막염 (78.9% 유사)\\n• 주요 증상: 결막염 (92.1% 유사)\\n• 발생 패턴: 결막염 (88.7% 유사)\\n\\n📊 전체 유사도 분석:\\n• 결막염: 86.2%\\n• 비궤양성 각막염: 45.3%\\n• 안검염: 32.1%\","
                + "  \"details\": \"환자의 증상과 관찰된 징후를 종합적으로 분석한 결과, 알레르기성 결막염으로 진단됩니다. 안구 표면의 점상 출혈과 결막 부종, 그리고 눈물 분비량 감소가 주요 소견으로 확인되었습니다. 적절한 항염 치료와 함께 알레르기 원인 회피가 권장됩니다.\","
                + "  \"attribute_analysis\": {"
                + "    \"분비물 특성\": {"
                + "      \"llm_analysis\": \"# 1. 비궤양성 각막염와 유사성\\n\\n제공된 정보에 따르면 비궤양성 각막염과 85.7% 일치합니다.\""
                + "    },"
                + "    \"진행 속도\": {"
                + "      \"llm_analysis\": \"# 1. 비궤양성 각막염와 유사성\\n\\n제공된 정보에 따르면 비궤양성 각막염과 84.5% 일치합니다.\""
                + "    },"
                + "    \"주요 증상\": {"
                + "      \"llm_analysis\": \"# 1. 비궤양성 각막염와 유사성\\n\\n제공된 정보에 따르면 비궤양성 각막염과 90.6% 일치합니다.\""
                + "    },"
                + "    \"발생 패턴\": {"
                + "      \"llm_analysis\": \"# 1. 결막염과의 유사성\\n\\n제공된 정보에 따르면 결막염과 82.7% 일치합니다.\""
                + "    }"
                + "  }"
                + "}"
                + "}";
        ResponseEntity<String> mockThirdStepResponseEntity = 
                new ResponseEntity<>(mockThirdStepResponse, HttpStatus.OK);
        
        when(restTemplate.postForEntity(
                eq(aiServerUrl + "/diagnosis/step3/"),
                any(),
                eq(String.class)))
                .thenReturn(mockThirdStepResponseEntity);
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
        PostSecondStepDiagnosisRequest createSecondStepDiagnosisRequest = PostSecondStepDiagnosisRequest.builder()
            .id(diagnosisId)
            .category("testCategory")
            .confidence(0.95)
            .password(password)
            .build();

        // Then
        mockMvc.perform(post("/diagnosis/step2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createSecondStepDiagnosisRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(true)); // 성공 시 데이터는 비어있음

        // 진단 결과 조회
        mockMvc.perform(get("/diagnosis/step2/" + diagnosisId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.category").value(createSecondStepDiagnosisRequest.getCategory()))
            .andExpect(jsonPath("$.data.confidence").value(createSecondStepDiagnosisRequest.getConfidence()))
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
        PostSecondStepDiagnosisRequest updateSecondDiagnosisRequest = PostSecondStepDiagnosisRequest.builder()
            .id(diagnosisId)
            .category("testCategory")
            .confidence(0.95)
            .password("wrong" + password) // 잘못된 패스워드
            .build();
        
        // Then
        mockMvc.perform(post("/diagnosis/step2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateSecondDiagnosisRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("A001")); // UNAUTHORIZED 에러 코드
    }

    @Test
    @DisplayName("진단 규칙 조회 API 성공")
    @WithMockUser(username = USER_ID)
    void getDiagnosisRules_Success() throws Exception {
        mockMvc.perform(get("/diagnosis/rules")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rules").isArray())
                .andExpect(jsonPath("$.data.rules[0].id").exists())
                .andExpect(jsonPath("$.data.rules[0].name").exists());
    }

    @Test
    @DisplayName("진단 3단계 생성 API 성공")
    @WithMockUser(username = USER_ID)
    void createThirdStepDiagnosis_Success() throws Exception {
        // Given
        FirstStepDiagnosis firstStepDiagnosis = FirstStepDiagnosis.builder()
                .imageUrl("https://s3.bucket/path/to/image.jpg")
                .isNormal(true)
                .confidence(0.9999570846557617)
                .passwordForSecondStep("password")
                .build();
        SecondStepDiagnosis secondStepDiagnosis = new SecondStepDiagnosis(
            firstStepDiagnosis, "password", "category", 0.5);
        secondStepDiagnosisRepository.save(secondStepDiagnosis);
        
        // When & Then
        PostThirdStepDiagnosisRequest request = new PostThirdStepDiagnosisRequest(
                secondStepDiagnosis.getId(),
                List.of(
                        new PostThirdStepDiagnosisRequest.UserResponse("1", "점액성")
                )
        );
        mockMvc.perform(post("/diagnosis/step3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.category").exists())
                .andExpect(jsonPath("$.data.summary").exists())
                .andExpect(jsonPath("$.data.details").exists())
                .andExpect(jsonPath("$.data.attributeAnalysis").exists());
    }
}
