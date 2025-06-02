package com.annyang.diagnosis.service;

import com.annyang.auth.exception.UnauthorizedException;
import com.annyang.diagnosis.client.AiServerClient;
import com.annyang.diagnosis.client.DiagnosisDto.FirstStepResponse;
import com.annyang.diagnosis.dto.UpdateSecondDiagnosisRequest;
import com.annyang.diagnosis.dto.DiagnosisRequest;
import com.annyang.diagnosis.dto.DiagnosisResponse;
import com.annyang.diagnosis.dto.SecondDiagnosisResponse;
import com.annyang.diagnosis.entity.Diagnosis;
import com.annyang.diagnosis.repository.DiagnosisRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final AiServerClient aiServerClient;
    private final DiagnosisRepository diagnosisRepository;

    @Transactional
    public DiagnosisResponse diagnoseFirstStep(DiagnosisRequest request) {
        FirstStepResponse response = aiServerClient.requestFirstDiagnosis(request.getImageUrl());

        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 30);

        Diagnosis diagnosis = Diagnosis.builder()
                .id(id)
                .imageUrl(request.getImageUrl())
                .normal(response.isNormal())
                .confidence(response.getConfidence()) // 2차 진단 신뢰도 초기화
                .build();
        diagnosisRepository.save(diagnosis);

        return DiagnosisResponse.builder()
                .id(id)
                .normal(response.isNormal())
                .confidence(response.getConfidence())
                .build();
    }

    @Transactional
    public boolean requestSecondStepDiagnosis(String id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found with id: " + id));
        String imageUrl = diagnosis.getImageUrl();
        String password = UUID.randomUUID().toString();
        diagnosis.setPassword(hashPassword(password));
        diagnosisRepository.save(diagnosis);
        return aiServerClient.requestSecondDiagnosis(id, password, imageUrl);
    }

    @Transactional
    public boolean updateSecondDiagnosis(UpdateSecondDiagnosisRequest request) {
        Diagnosis diagnosis = diagnosisRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found with id: " + request.getId()));

        if(!verifyPassword(diagnosis, request.getPassword())) {
            throw new UnauthorizedException();
        }

        diagnosis.updateSecondDiagnosis(request.getCategory(), request.getConfidence());
        diagnosisRepository.save(diagnosis);
        return true;
    }

    @Transactional(readOnly = true)
    public SecondDiagnosisResponse getSecondDiagnosis(String id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found with id: " + id));

        return SecondDiagnosisResponse.builder()
                .id(id)
                .category(diagnosis.getCategory())
                .confidence(diagnosis.getConfidenceOfSecond())
                .build();
    }

    private String hashPassword(String password) {
        return java.util.Base64.getEncoder().encodeToString(password.getBytes());
    }

    // DB에 해싱되어서 저장된 pasword 일치여부 검증
    private boolean verifyPassword(Diagnosis diagnosis, String password) {
        String storedHashedPassword = diagnosis.getPassword();
        if (storedHashedPassword == null) { // 비밀번호가 설정되지 않은 경우
            return false;
        }
        String hashedInputPassword = hashPassword(password); // 입력된 비밀번호 해싱
        return storedHashedPassword.equals(hashedInputPassword);
    }

    public SecondDiagnosisResponse getSecondDiagnosisResponse(String id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found with id: " + id));
        return SecondDiagnosisResponse.builder()
                .id(id)
                .category(diagnosis.getCategory())
                .confidence(diagnosis.getConfidenceOfSecond())
                .build();
    }
}