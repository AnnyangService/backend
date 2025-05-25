package com.annyang.storage.controller;

import com.annyang.global.response.ApiResponse;
import com.annyang.storage.infrastructure.dto.PreSignedUrlResponse;
import com.annyang.storage.infrastructure.service.S3Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {
    private final S3Service s3Service;

    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PreSignedUrlResponse>> getPreSignedUrl(
            @RequestParam String fileName,
            @RequestParam String contentType) {
        PreSignedUrlResponse response = s3Service.generatePreSignedUrl(fileName, contentType);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}