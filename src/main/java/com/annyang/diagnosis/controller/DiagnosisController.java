package com.annyang.diagnosis.controller;

import com.annyang.diagnosis.dto.UpdateSecondDiagnosisRequest;
import com.annyang.diagnosis.dto.DiagnosisRequest;
import com.annyang.diagnosis.dto.DiagnosisResponse;
import com.annyang.diagnosis.dto.SecondDiagnosisResponse;
import com.annyang.diagnosis.service.DiagnosisService;
import com.annyang.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {
    
    private final DiagnosisService diagnosisService;
    
    @PostMapping("/step1")
    public ResponseEntity<ApiResponse<DiagnosisResponse>> diagnosisFirstStep(
            @Valid @RequestBody DiagnosisRequest request) {
        DiagnosisResponse response = diagnosisService.diagnoseFirstStep(request);
        diagnosisService.requestSecondStepDiagnosis(response.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/step2/{id}")
    public ResponseEntity<ApiResponse<SecondDiagnosisResponse>> getDiagnosisSecondStep(
            @PathVariable String id) {
        SecondDiagnosisResponse response = diagnosisService.getSecondDiagnosis(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/step2")
    public ResponseEntity<ApiResponse<Boolean>> updateDiagnosisSecondStep(
            @Valid @RequestBody UpdateSecondDiagnosisRequest request) {
        boolean isUpdated = diagnosisService.updateSecondDiagnosis(request);
        return ResponseEntity.ok(ApiResponse.success(isUpdated));
    }
}
