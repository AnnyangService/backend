package com.annyang.storage.infrastructure.service;

import com.annyang.storage.infrastructure.config.S3Config;
import com.annyang.storage.infrastructure.dto.PreSignedUrlResponse;
import com.annyang.storage.infrastructure.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final S3Config s3Config;
    
    private static final Duration PRE_SIGNED_URL_DURATION = Duration.ofMinutes(10);
    
    public PreSignedUrlResponse generatePreSignedUrl(String originalFileName, String contentType) {
        try {
            String fileName = createUniqueFileName(originalFileName);
            
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getS3().getBucket())
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r -> r
                    .signatureDuration(PRE_SIGNED_URL_DURATION)
                    .putObjectRequest(objectRequest));

            return new PreSignedUrlResponse(
                presignedRequest.url().toString(),
                fileName,
                contentType
            );
        } catch (Exception e) {
            log.error("Failed to generate pre-signed URL", e);
            throw new StorageException("Failed to generate pre-signed URL", e);
        }
    }

    private String createUniqueFileName(String originalFileName) {
        String extension = extractFileExtension(originalFileName);
        return UUID.randomUUID().toString() + extension;
    }

    private String extractFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : "";
    }
}