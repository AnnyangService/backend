package com.annyang.diagnosis.service;

import com.annyang.diagnosis.client.AiServerClient;
import com.annyang.diagnosis.dto.api.PostFirstStepDiagnosisRequest;
import com.annyang.diagnosis.dto.api.PostFirstStepDiagnosisResponse;
import com.annyang.diagnosis.dto.ai.PostFirstStepDiagnosisToAiResponse;
import com.annyang.diagnosis.dto.api.GetSecondStepDiagnosisResponse;
import com.annyang.diagnosis.dto.api.UpdateSecondStepDiagnosisRequest;
import com.annyang.diagnosis.entity.FirstStepDiagnosis;
import com.annyang.diagnosis.entity.SecondStepDiagnosis;
import com.annyang.diagnosis.repository.SecondStepDiagnosisRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final AiServerClient aiServerClient;
    private final SecondStepDiagnosisRepository secondStepDiagnosisRepository;

    @Transactional
    public PostFirstStepDiagnosisResponse diagnoseFirstStep(PostFirstStepDiagnosisRequest request) {
        PostFirstStepDiagnosisToAiResponse response = aiServerClient.requestFirstDiagnosis(request.getImageUrl());

        FirstStepDiagnosis firstStepDiagnosis = FirstStepDiagnosis.builder()
                .imageUrl(request.getImageUrl())
                .isNormal(response.isNormal())
                .confidence(response.getConfidence())
                .build();

        String password = "password"; // 테스트용 비밀번호, 실제로는 UUID로 생성해야 함
        SecondStepDiagnosis secondStepDiagnosis = new SecondStepDiagnosis(firstStepDiagnosis, password);

        secondStepDiagnosisRepository.save(secondStepDiagnosis);
        
        aiServerClient.requestSecondDiagnosis(
                secondStepDiagnosis.getId(), 
                password, 
                firstStepDiagnosis.getImageUrl());

        return PostFirstStepDiagnosisResponse.builder()
                .id(firstStepDiagnosis.getId())
                .normal(firstStepDiagnosis.isNormal())
                .confidence(firstStepDiagnosis.getConfidence())
                .build();
    }

    @Transactional
    public boolean updateSecondDiagnosis(UpdateSecondStepDiagnosisRequest request) {
        SecondStepDiagnosis secondDiagnosis = secondStepDiagnosisRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("SecondStepDiagnosis not found with id: " + request.getId()));
        secondDiagnosis.updateDiagnosis(request.getPassword(), request.getCategory(), request.getConfidence());
        secondStepDiagnosisRepository.save(secondDiagnosis);
        return true;
    }

    @Transactional(readOnly = true)
    public GetSecondStepDiagnosisResponse getSecondDiagnosis(String id) {
        SecondStepDiagnosis secondDiagnosis = secondStepDiagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SecondStepDiagnosis not found with id: " + id));

        if(secondDiagnosis.getCategory() == null) return null;
        
        return GetSecondStepDiagnosisResponse.builder()
                .id(id)
                .category(secondDiagnosis.getCategory())
                .confidence(secondDiagnosis.getConfidence())
                .build();
    }
}