package com.annyang.diagnosis.service;

import com.annyang.diagnosis.client.AiServerClient;
import com.annyang.diagnosis.dto.api.PostFirstStepDiagnosisRequest;
import com.annyang.diagnosis.dto.api.PostFirstStepDiagnosisResponse;
import com.annyang.diagnosis.dto.ai.PostFirstStepDiagnosisToAiResponse;
import com.annyang.diagnosis.dto.api.PostSecondStepDiagnosisRequest;
import com.annyang.diagnosis.dto.api.GetSecondStepDiagnosisResponse;
import com.annyang.diagnosis.entity.FirstStepDiagnosis;
import com.annyang.diagnosis.entity.SecondStepDiagnosis;
import com.annyang.diagnosis.repository.FirstStepDiagnosisRepository;
import com.annyang.diagnosis.repository.SecondStepDiagnosisRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final AiServerClient aiServerClient;
    private final FirstStepDiagnosisRepository firstStepDiagnosisRepository;
    private final SecondStepDiagnosisRepository secondStepDiagnosisRepository;

    @Transactional
    public PostFirstStepDiagnosisResponse diagnoseFirstStep(PostFirstStepDiagnosisRequest request) {
        PostFirstStepDiagnosisToAiResponse response = aiServerClient.requestFirstDiagnosis(request.getImageUrl());
        /**
        TODO: AI 서버 구현 완료 후 비밀번호 생성 로직을 UUID로 변경
        String passwordForSecondStep = UUID.randomUUID().toString();
         */
        String passwordForSecondStep = "password";

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
}