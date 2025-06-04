package com.annyang.diagnosis.service;

import com.annyang.diagnosis.client.AiServerClient;
import com.annyang.diagnosis.dto.api.PostFirstStepDiagnosisRequest;
import com.annyang.diagnosis.dto.api.PostFirstStepDiagnosisResponse;
import com.annyang.diagnosis.dto.ai.PostFirstStepDiagnosisToAiResponse;
import com.annyang.diagnosis.dto.ai.PostThirdStepDiagnosisToAiResponse;
import com.annyang.diagnosis.dto.api.PostSecondStepDiagnosisRequest;
import com.annyang.diagnosis.dto.api.PostThirdStepDiagnosisRequest;
import com.annyang.diagnosis.dto.api.PostThirdStepDiagnosisResponse;
import com.annyang.diagnosis.dto.api.GetDiagnosisRuleResponse;
import com.annyang.diagnosis.dto.api.GetSecondStepDiagnosisResponse;
import com.annyang.diagnosis.entity.DiagnosisRule;
import com.annyang.diagnosis.entity.FirstStepDiagnosis;
import com.annyang.diagnosis.entity.SecondStepDiagnosis;
import com.annyang.diagnosis.entity.ThirdStepDiagnosis;
import com.annyang.diagnosis.repository.DiagnosisRuleRepository;
import com.annyang.diagnosis.repository.FirstStepDiagnosisRepository;
import com.annyang.diagnosis.repository.SecondStepDiagnosisRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.annyang.diagnosis.repository.ThirdStepDiagnosisRepository;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final AiServerClient aiServerClient;
    private final FirstStepDiagnosisRepository firstStepDiagnosisRepository;
    private final SecondStepDiagnosisRepository secondStepDiagnosisRepository;
    private final ThirdStepDiagnosisRepository thirdStepDiagnosisRepository;
    private final DiagnosisRuleRepository diagnosisRuleRepository;

    @Transactional
    public PostFirstStepDiagnosisResponse diagnoseFirstStep(PostFirstStepDiagnosisRequest request) {
        PostFirstStepDiagnosisToAiResponse response = aiServerClient.requestFirstDiagnosis(request.getImageUrl());
        String passwordForSecondStep = UUID.randomUUID().toString();

        FirstStepDiagnosis firstStepDiagnosis = FirstStepDiagnosis.builder()
                .imageUrl(request.getImageUrl())
                .isNormal(response.isNormal())
                .confidence(response.getConfidence())
                .passwordForSecondStep(passwordForSecondStep)
                .build();
        firstStepDiagnosisRepository.save(firstStepDiagnosis);

        aiServerClient.requestSecondDiagnosis(
                firstStepDiagnosis.getId(), 
                passwordForSecondStep, 
                firstStepDiagnosis.getImageUrl());

        return PostFirstStepDiagnosisResponse.builder()
                .id(firstStepDiagnosis.getId())
                .normal(firstStepDiagnosis.isNormal())
                .confidence(firstStepDiagnosis.getConfidence())
                .build();
    }

    @Transactional
    public void createSecondStepDiagnosis(PostSecondStepDiagnosisRequest request) {
        FirstStepDiagnosis firstStepDiagnosis = firstStepDiagnosisRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("FirstStepDiagnosis not found with id: " + request.getId()));
        SecondStepDiagnosis secondStepDiagnosis = new SecondStepDiagnosis(
                firstStepDiagnosis,
                request.getPassword(), 
                request.getCategory(),
                request.getConfidence());
        secondStepDiagnosisRepository.save(secondStepDiagnosis);
    }

    @Transactional(readOnly = true)
    public GetSecondStepDiagnosisResponse getSecondDiagnosis(String id) {
        SecondStepDiagnosis secondDiagnosis = secondStepDiagnosisRepository.findById(id)
                .orElse(null);
        if (secondDiagnosis == null) return null;

        return GetSecondStepDiagnosisResponse.builder()
                .id(id)
                .category(secondDiagnosis.getCategory())
                .confidence(secondDiagnosis.getConfidence())
                .build();
    }

    public GetDiagnosisRuleResponse getDiagnosisRules() {
        // TDOO DB에 저장해서 사용하도록 변경 필요
        // System.out.println("Diagnosis rules initialized for local testing.");
        List<DiagnosisRule> rules = List.of(
                DiagnosisRule.builder().id(1).name("분비물 특성").build(),
                DiagnosisRule.builder().id(2).name("진행 속도").build(),
                DiagnosisRule.builder().id(3).name("주요 증상").build(),
                DiagnosisRule.builder().id(4).name("발생패턴").build()
        );
        return GetDiagnosisRuleResponse.builder()
                .rules(rules)
                .build();
    }

    @Transactional
    public PostThirdStepDiagnosisResponse createThirdStepDiagnosis(PostThirdStepDiagnosisRequest request) {
        FirstStepDiagnosis firstStepDiagnosis = firstStepDiagnosisRepository.findById(request.getDiagnosisId())
                .orElseThrow(() -> new EntityNotFoundException("FirstStepDiagnosis not found with id: " + request.getDiagnosisId()));
        SecondStepDiagnosis secondStepDiagnosis = secondStepDiagnosisRepository.findById(firstStepDiagnosis.getId())
                .orElseThrow(() -> new EntityNotFoundException("SecondStepDiagnosis not found for FirstStepDiagnosis with id: " + request.getDiagnosisId()));

        PostThirdStepDiagnosisToAiResponse response = aiServerClient.requestThirdDiagnosis(secondStepDiagnosis.getCategory(), request.getUserResponses());

        ThirdStepDiagnosis thirdStepDiagnosis = ThirdStepDiagnosis.builder()
                .firstStepDiagnosis(firstStepDiagnosis)
                .category(response.getCategory())
                .confidence(response.getConfidence())
                .build();
        thirdStepDiagnosisRepository.save(thirdStepDiagnosis);

        return PostThirdStepDiagnosisResponse.builder()
                .id(thirdStepDiagnosis.getId())
                .category(thirdStepDiagnosis.getCategory())
                .confidence(thirdStepDiagnosis.getConfidence())
                .build();
    }
}