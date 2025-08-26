package com.annyang.diagnosis.controller;

import com.annyang.diagnosis.dto.GetDiagnosisRuleResponse;
import com.annyang.diagnosis.dto.GetSecondStepDiagnosisResponse;
import com.annyang.diagnosis.dto.PostFirstStepDiagnosisRequest;
import com.annyang.diagnosis.dto.PostFirstStepDiagnosisResponse;
import com.annyang.diagnosis.dto.PostSecondStepDiagnosisRequest;
import com.annyang.diagnosis.dto.PostThirdStepDiagnosisRequest;
import com.annyang.diagnosis.dto.PostThirdStepDiagnosisResponse;
import com.annyang.diagnosis.service.DiagnosisService;
import com.annyang.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {
    
    private final DiagnosisService diagnosisService;
    
    @PostMapping("/step1")
    public ResponseEntity<ApiResponse<PostFirstStepDiagnosisResponse>> diagnosisFirstStep(
            @Valid @RequestBody PostFirstStepDiagnosisRequest request) {
        PostFirstStepDiagnosisResponse response = diagnosisService.diagnoseFirstStep(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/step2/{id}")
    public ResponseEntity<ApiResponse<GetSecondStepDiagnosisResponse>> getDiagnosisSecondStep(
            @PathVariable String id) {
        GetSecondStepDiagnosisResponse response = diagnosisService.getSecondDiagnosis(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/step2")
    public ResponseEntity<ApiResponse<Boolean>> createSecondStepDiagnosis(
            @Valid @RequestBody PostSecondStepDiagnosisRequest request) {
        System.out.println("Received request for second step diagnosis: " + request);
        diagnosisService.createSecondStepDiagnosis(request);
        return ResponseEntity.ok(ApiResponse.success(true));
    }

    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<GetDiagnosisRuleResponse>> getDiagnosisRules() {
        GetDiagnosisRuleResponse response = diagnosisService.getDiagnosisRules();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/step3")
    public ResponseEntity<ApiResponse<PostThirdStepDiagnosisResponse>> postThirdStepDiagnosis(
            @Valid @RequestBody PostThirdStepDiagnosisRequest request) {
        PostThirdStepDiagnosisResponse response = diagnosisService.createThirdStepDiagnosis(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
