package com.annyang.diagnosis.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@AllArgsConstructor
@Getter
@Schema(description = "3단계 진단 요청")
public class PostThirdStepDiagnosisRequest {
    
    @Schema(description = "2단계 진단 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotBlank(message = "진단 ID는 필수입니다")
    private String diagnosisId;
    
    @Schema(description = "사용자 응답 목록")
    @NotEmpty(message = "사용자 응답은 최소 1개 이상이어야 합니다")
    @Valid
    private List<UserResponse> userResponses;

    @AllArgsConstructor
    @Getter
    @Schema(description = "사용자 응답")
    public static class UserResponse {
        
        @Schema(description = "진단 규칙 ID", example = "1")
        @NotBlank(message = "진단 규칙 ID는 필수입니다")
        private String diagnosisRuleId;
        
        @Schema(description = "사용자 답변", example = "점액성")
        @NotBlank(message = "사용자 답변은 필수입니다")
        private String userResponse;
    }
}

