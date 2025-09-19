package com.annyang.infrastructure.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.annyang.chatbot.entity.ChatbotConversation;
import com.annyang.chatbot.entity.ChatbotSession;
import com.annyang.diagnosis.entity.FirstStepDiagnosis;
import com.annyang.diagnosis.entity.ThirdStepDiagnosis;
import com.annyang.infrastructure.client.dto.PostChatbotQueryToAiResponse;
import com.annyang.infrastructure.client.dto.PostChatbotSessionToAiResponse;
import com.annyang.member.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@Tag("manual")
public class AiServerClientTest {
    @Autowired
    private AiServerClient aiServerClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testCreateChatbotSession_Success() {
        // Given
        FirstStepDiagnosis firstStepDiagnosis = FirstStepDiagnosis.builder()
            .imageUrl("http://example.com/image.jpg")
            .isNormal(false)
            .confidence(0.85)
            .passwordForSecondStep("testpw")
            .build();
        HashMap<String, ThirdStepDiagnosis.AttributeAnalysis> attributeAnalysisMap = new HashMap<>();
        attributeAnalysisMap.put("분비물 특성", ThirdStepDiagnosis.AttributeAnalysis.builder()
            .llmAnalysis("눈물 과다 분비와 미세한 분비물은 비궤양성 각막염의 특징적인 증상입니다. 또한, 서서히 진행되는 양상도 이 질환과 일치합니다.")
            .build());
        ThirdStepDiagnosis thirdStepDiagnosis = ThirdStepDiagnosis.builder()
            .firstStepDiagnosis(firstStepDiagnosis)
            .category("비궤양성 각막염")
            .summary("🔍 진단 결과: 비궤양성 각막염\n• 분비물 특성: 비궤양성 각막염 (85.7% 유사)\n• 진행 속도: 비궤양성 각막염 (84.5% 유사)\n• 주요 증상: 비궤양성 각막염 (90.6% 유사)")
            .details("# 비궤양성 각막염 진단 보고서\n\n제공된 정보를 바탕으로 비궤양성 각막염이 의심됩니다. 눈물 과다 분비, 미세한 분비물, 서서히 진행되는 양상 등이 주요 근거입니다. ")
            .attributeAnalysis(attributeAnalysisMap)
            .build();

        // When
        PostChatbotSessionToAiResponse response = aiServerClient.createChatbotSession("이 질병은 어떻게 치료하나요?", thirdStepDiagnosis);

        // Then
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("AI Server Response: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(response);
        assertNotNull(response.getAnswer());
    }

    @Test
    void testSubmitChatbotQuery_Success() {
        // Given
        Member testMember = new Member(
            "test@example.com",
            passwordEncoder.encode("password123"),
            "Test User"
        );
        FirstStepDiagnosis firstStepDiagnosis = FirstStepDiagnosis.builder()
            .imageUrl("http://example.com/image.jpg")
            .isNormal(false)
            .confidence(0.85)
            .passwordForSecondStep("testpw")
            .build();
        HashMap<String, ThirdStepDiagnosis.AttributeAnalysis> attributeAnalysisMap = new HashMap<>();
        attributeAnalysisMap.put("분비물 특성", ThirdStepDiagnosis.AttributeAnalysis.builder()
            .llmAnalysis("눈물 과다 분비와 미세한 분비물은 비궤양성 각막염의 특징적인 증상입니다. 또한, 서서히 진행되는 양상도 이 질환과 일치합니다.")
            .build());
        ThirdStepDiagnosis thirdStepDiagnosis = ThirdStepDiagnosis.builder()
            .firstStepDiagnosis(firstStepDiagnosis)
            .category("비궤양성 각막염")
            .summary("🔍 진단 결과: 비궤양성 각막염\n• 분비물 특성: 비궤양성 각막염 (85.7% 유사)\n• 진행 속도: 비궤양성 각막염 (84.5% 유사)\n• 주요 증상: 비궤양성 각막염 (90.6% 유사)")
            .details("# 비궤양성 각막염 진단 보고서\n\n제공된 정보를 바탕으로 비궤양성 각막염이 의심됩니다. 눈물 과다 분비, 미세한 분비물, 서서히 진행되는 양상 등이 주요 근거입니다. ")
            .attributeAnalysis(attributeAnalysisMap)
            .build();
        ChatbotSession chatbotSession = new ChatbotSession(testMember, thirdStepDiagnosis);
        List<ChatbotConversation> conversationHistory = new ArrayList<>();
        conversationHistory.add(ChatbotConversation.builder().chatbotSession(chatbotSession).question("이 질병은 무엇인가요?").answer("비궤양성 각막염은 각막의 염증성 질환입니다.").build());
        conversationHistory.add(ChatbotConversation.builder().chatbotSession(chatbotSession).question("증상이 심각한가요?").answer("현재 증상은 중간 정도의 심각도를 보입니다.").build());

        // When
        PostChatbotQueryToAiResponse response = aiServerClient.submitChatbotQuery("이 질병은 어떻게 치료하나요?", thirdStepDiagnosis.getCategory(), conversationHistory);

        // Then
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("AI Server Response: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(response);
        assertNotNull(response.getAnswer());
    }
}
