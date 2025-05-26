package com.annyang.diagnosis.controller;

import com.annyang.diagnosis.dto.DiagnosisRequest;
import com.annyang.diagnosis.dto.DiagnosisResponse;
import com.annyang.diagnosis.service.DiagnosisService;
import com.annyang.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<DiagnosisResponse>> diagnosisFirstStep(
            @Valid @RequestBody DiagnosisRequest request) {
        DiagnosisResponse response = diagnosisService.diagnoseFirstStep(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
