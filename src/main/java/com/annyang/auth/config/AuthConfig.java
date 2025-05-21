package com.annyang.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {
    public static class RefreshToken {
        public static final String NAME = "refresh_token";
        public static final String PATH = "/auth/refresh";
        public static final int MAX_AGE = 14 * 24 * 60 * 60; // 14일
    }
}