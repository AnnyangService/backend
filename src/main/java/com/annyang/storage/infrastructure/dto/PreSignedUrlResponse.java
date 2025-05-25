package com.annyang.storage.infrastructure.dto;

import lombok.Getter;

@Getter
public class PreSignedUrlResponse {
    private final String preSignedUrl;
    private final String fileName;
    private final String contentType;

    public PreSignedUrlResponse(String preSignedUrl, String fileName, String contentType) {
        this.preSignedUrl = preSignedUrl;
        this.fileName = fileName;
        this.contentType = contentType;
    }
}