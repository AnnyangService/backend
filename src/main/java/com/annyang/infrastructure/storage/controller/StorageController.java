package com.annyang.infrastructure.storage.controller;

import com.annyang.global.response.ApiResponse;
import com.annyang.infrastructure.storage.dto.GeneratePresignedUrlRequest;
import com.annyang.infrastructure.storage.dto.PreSignedUrlResponse;
import com.annyang.infrastructure.storage.service.S3Service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {
    private final S3Service s3Service;

    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PreSignedUrlResponse>> getPreSignedUrl(
            @Valid @RequestBody GeneratePresignedUrlRequest request) {
        PreSignedUrlResponse response = s3Service.generatePreSignedUrl(request.getFileName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}