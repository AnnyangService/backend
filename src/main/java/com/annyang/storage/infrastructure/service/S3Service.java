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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final S3Config s3Config;
    
    private static final Duration PRE_SIGNED_URL_DURATION = Duration.ofMinutes(10);
    private static final Map<String, String> CONTENT_TYPE_MAP = new HashMap<>();
    
    static {
        // 이미지 형식별 MIME 타입 매핑
        CONTENT_TYPE_MAP.put("jpg", "image/jpeg");
        CONTENT_TYPE_MAP.put("jpeg", "image/jpeg");
        CONTENT_TYPE_MAP.put("png", "image/png");
        CONTENT_TYPE_MAP.put("gif", "image/gif");
        CONTENT_TYPE_MAP.put("bmp", "image/bmp");
        CONTENT_TYPE_MAP.put("webp", "image/webp");
        CONTENT_TYPE_MAP.put("svg", "image/svg+xml");
    }
    
    public PreSignedUrlResponse generatePreSignedUrl(String originalFileName) {
        try {
            String fileName = createUniqueFileName(originalFileName);
            String contentType = getContentTypeByFileName(fileName);
            
            // 객체에 ACL 설정 추가 (공개 읽기 권한)
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getS3().getBucket())
                    .key(fileName)
                    .contentType(contentType)
                    .acl("public-read")  // 이 부분 추가
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r -> r
                    .signatureDuration(PRE_SIGNED_URL_DURATION)
                    .putObjectRequest(objectRequest));

            // 객체 URL 구성
            String objectUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
                    s3Config.getS3().getBucket(), 
                    s3Config.getRegion(), 
                    fileName);
                    
            return new PreSignedUrlResponse(
                presignedRequest.url().toString(),
                fileName,
                contentType,
                objectUrl  // 직접 접근할 수 있는 URL 추가
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
    
    private String getContentTypeByFileName(String fileName) {
        String extension = extractFileExtension(fileName).toLowerCase();
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        
        return CONTENT_TYPE_MAP.getOrDefault(extension, "application/octet-stream");
    }
}