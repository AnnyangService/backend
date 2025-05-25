package com.annyang.storage.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratePresignedUrlRequest {
    @NotBlank(message = "파일명은 필수입니다")
    private String fileName;
    
    // 향후 확장 가능한 필드들
    // private String directory;
    // private Long maxFileSize;
    // private String purpose;  // "profile", "product", "thumbnail" 등
}