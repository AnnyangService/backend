package com.annyang.diagnosis.service;

import com.annyang.diagnosis.client.AiServerClient;
import com.annyang.diagnosis.dto.api.PostFirstStepDiagnosisRequest;
import com.annyang.diagnosis.dto.api.PostFirstStepDiagnosisResponse;
import com.annyang.diagnosis.dto.ai.PostFirstStepDiagnosisToAiResponse;
import com.annyang.diagnosis.dto.api.GetSecondStepDiagnosisResponse;
import com.annyang.diagnosis.dto.api.UpdateSecondStepDiagnosisRequest;
import com.annyang.diagnosis.entity.FirstStepDiagnosis;
import com.annyang.diagnosis.entity.SecondStepDiagnosis;
import com.annyang.diagnosis.repository.FirstStepDiagnosisRepository;
import com.annyang.diagnosis.repository.SecondStepDiagnosisRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final AiServerClient aiServerClient;
    private final FirstStepDiagnosisRepository firstStepDiagnosisRepository;
    private final SecondStepDiagnosisRepository secondStepDiagnosisRepository;

    @Transactional
    public PostFirstStepDiagnosisResponse diagnoseFirstStep(PostFirstStepDiagnosisRequest request) {
        PostFirstStepDiagnosisToAiResponse response = aiServerClient.requestFirstDiagnosis(request.getImageUrl());

        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 30);

        FirstStepDiagnosis firstStepDiagnosis = FirstStepDiagnosis.builder()
                .id(id)
                .imageUrl(request.getImageUrl())
                .isNormal(response.isNormal())
                .confidence(response.getConfidence())
                .build();
        firstStepDiagnosisRepository.save(firstStepDiagnosis);

        return PostFirstStepDiagnosisResponse.builder()
                .id(id)
                .normal(response.isNormal())
                .confidence(response.getConfidence())
                .build();
    }

    @Transactional
    public boolean requestSecondStepDiagnosis(String id) {
        FirstStepDiagnosis firstStepDiagnosis = firstStepDiagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FirstStepDiagnosis not found with id: " + id));
        String imageUrl = firstStepDiagnosis.getImageUrl();

        // TODO AI 서버 구현 완료 후 주석 제거
        // String password = UUID.randomUUID().toString();
        String password = "password"; // 테스트용 비밀번호, 실제로는 UUID로 생성해야 함
        SecondStepDiagnosis secondStepDiagnosis = SecondStepDiagnosis.builder()
                .id(id)
                .password(password)
                .build();
        secondStepDiagnosisRepository.save(secondStepDiagnosis);

        return aiServerClient.requestSecondDiagnosis(id, password, imageUrl);
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