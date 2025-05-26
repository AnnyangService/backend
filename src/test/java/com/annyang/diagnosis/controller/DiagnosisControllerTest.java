package com.annyang.diagnosis.controller;

import com.annyang.Main;
import com.annyang.diagnosis.dto.DiagnosisRequest;
import com.annyang.diagnosis.dto.DiagnosisResponse;
import com.annyang.diagnosis.service.DiagnosisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private DiagnosisService diagnosisService;

    private DiagnosisRequest diagnosisRequest;
    private final String USER_ID = "01HXSAXEAASYJY0VZ7J2VPHCX8";
    private final String FIXED_ID = "01JTTKJYG28CFYMBKXC0Q80F61";

    @BeforeEach
    void setUp() {
        diagnosisRequest = new DiagnosisRequest();
        diagnosisRequest.setImageUrl("https://s3.bucket/path/to/image.jpg");
        
        // Mock the service response
        when(diagnosisService.diagnoseFirstStep(any(DiagnosisRequest.class)))
                .thenReturn(DiagnosisResponse.builder()
                        .id(FIXED_ID)
                        .isNormal(true)
                        .confidence(0.9999570846557617)
                        .build());
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
                .andExpect(jsonPath("$.data.id").value(FIXED_ID))
                .andExpect(jsonPath("$.data.is_normal").value(true))
                .andExpect(jsonPath("$.data.confidence").value(0.9999570846557617));
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
}
