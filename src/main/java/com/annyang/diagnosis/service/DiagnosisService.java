package com.annyang.diagnosis.service;

import com.annyang.infrastructure.client.AiServerClient;
import com.annyang.infrastructure.client.dto.PostFirstStepDiagnosisToAiResponse;
import com.annyang.infrastructure.client.dto.PostThirdStepDiagnosisToAiResponse;
import com.annyang.diagnosis.dto.GetDiagnosisRuleResponse;
import com.annyang.diagnosis.dto.GetMyDiagnosesResponse;
import com.annyang.diagnosis.dto.GetSecondStepDiagnosisResponse;
import com.annyang.diagnosis.dto.PostFirstStepDiagnosisRequest;
import com.annyang.diagnosis.dto.PostFirstStepDiagnosisResponse;
import com.annyang.diagnosis.dto.PostSecondStepDiagnosisRequest;
import com.annyang.diagnosis.dto.PostThirdStepDiagnosisRequest;
import com.annyang.diagnosis.dto.PostThirdStepDiagnosisResponse;
import com.annyang.diagnosis.entity.DiagnosisRule;
import com.annyang.diagnosis.entity.FirstStepDiagnosis;
import com.annyang.diagnosis.entity.SecondStepDiagnosis;
import com.annyang.diagnosis.entity.ThirdStepDiagnosis;
import com.annyang.diagnosis.repository.DiagnosisRuleRepository;
import com.annyang.diagnosis.repository.FirstStepDiagnosisRepository;
import com.annyang.diagnosis.repository.SecondStepDiagnosisRepository;
import com.annyang.global.util.SecurityUtil;
import com.annyang.member.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final SecurityUtil securityUtil;

    @Transactional
    public PostFirstStepDiagnosisResponse diagnoseFirstStep(PostFirstStepDiagnosisRequest request) {
        Member currentMember = securityUtil.getCurrentMember();
        PostFirstStepDiagnosisToAiResponse response = aiServerClient.requestFirstDiagnosis(request.getImageUrl());
        String passwordForSecondStep = UUID.randomUUID().toString();

        FirstStepDiagnosis firstStepDiagnosis = FirstStepDiagnosis.builder()
                .member(currentMember)
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
        Member currentMember = securityUtil.getCurrentMember();
        SecondStepDiagnosis secondDiagnosis = secondStepDiagnosisRepository.findById(id)
                .orElse(null);
        if (secondDiagnosis == null) return null;
        
        // 현재 사용자가 해당 진단의 소유자인지 확인
        if (!secondDiagnosis.getFirstStepDiagnosis().getMember().getId().equals(currentMember.getId())) {
            throw new SecurityException("Access denied: You don't have permission to view this diagnosis");
        }

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
        Member currentMember = securityUtil.getCurrentMember();
        FirstStepDiagnosis firstStepDiagnosis = firstStepDiagnosisRepository.findById(request.getDiagnosisId())
                .orElseThrow(() -> new EntityNotFoundException("FirstStepDiagnosis not found with id: " + request.getDiagnosisId()));
        
        // 현재 사용자가 해당 진단의 소유자인지 확인
        if (!firstStepDiagnosis.getMember().getId().equals(currentMember.getId())) {
            throw new SecurityException("Access denied: You don't have permission to modify this diagnosis");
        }
        
        SecondStepDiagnosis secondStepDiagnosis = secondStepDiagnosisRepository.findById(firstStepDiagnosis.getId())
                .orElse(null);
        if (secondStepDiagnosis == null) {
            throw new EntityNotFoundException("SecondStepDiagnosis not found for FirstStepDiagnosis with id: " + request.getDiagnosisId());
        }

        ThirdStepDiagnosis thirdStepDiagnosis = thirdStepDiagnosisRepository.findById(firstStepDiagnosis.getId())
            .orElseGet(() -> {
                PostThirdStepDiagnosisToAiResponse response = aiServerClient.requestThirdDiagnosis(secondStepDiagnosis.getCategory(), request.getUserResponses());
                ThirdStepDiagnosis _thirdStepDiagnosis = new ThirdStepDiagnosis(firstStepDiagnosis, response);
                thirdStepDiagnosisRepository.save(_thirdStepDiagnosis);
                return _thirdStepDiagnosis;
            });
        return new PostThirdStepDiagnosisResponse(thirdStepDiagnosis);
    }

    // TODO N+1 성능 개선
    @Transactional(readOnly = true)
    public GetMyDiagnosesResponse getMyDiagnoses() {
        Member currentMember = securityUtil.getCurrentMember();
        List<FirstStepDiagnosis> firstStepDiagnoses = firstStepDiagnosisRepository.findAllByMemberIdOrderByCreatedAtDesc(currentMember.getId());
        
        List<GetMyDiagnosesResponse.DiagnosisDto> diagnosisDtos = firstStepDiagnoses.stream()
                .map(firstStep -> {
                    GetMyDiagnosesResponse.DiagnosisDto.DiagnosisDtoBuilder builder = GetMyDiagnosesResponse.DiagnosisDto.builder()
                            .id(firstStep.getId())
                            .imageUrl(firstStep.getImageUrl())
                            .isNormal(firstStep.isNormal())
                            .confidence(firstStep.getConfidence())
                            .createdAt(firstStep.getCreatedAt());
                    
                    // Second Step 정보 추가
                    SecondStepDiagnosis secondStep = secondStepDiagnosisRepository.findById(firstStep.getId()).orElse(null);
                    if (secondStep != null) {
                        builder.secondStep(GetMyDiagnosesResponse.SecondStepDto.builder()
                                .category(secondStep.getCategory())
                                .confidence(secondStep.getConfidence())
                                .createdAt(secondStep.getCreatedAt())
                                .build());
                        
                        // Third Step 정보 추가
                        ThirdStepDiagnosis thirdStep = thirdStepDiagnosisRepository.findById(firstStep.getId()).orElse(null);
                        if (thirdStep != null) {
                            builder.thirdStep(GetMyDiagnosesResponse.ThirdStepDto.builder()
                                    .category(thirdStep.getCategory())
                                    .summary(thirdStep.getSummary())
                                    .details(thirdStep.getDetails())
                                    .attributeAnalysis(thirdStep.getAttributeAnalysis())
                                    .createdAt(thirdStep.getCreatedAt())
                                    .build());
                        }
                    }
                    
                    return builder.build();
                })
                .collect(Collectors.toList());
        
        return GetMyDiagnosesResponse.builder()
                .diagnoses(diagnosisDtos)
                .build();
    }
}