package com.annyang.auth.config;

public final class AuthConfig {
    private AuthConfig() {
        throw new IllegalStateException("Utility class");
    }
    
    public static final class Cookie {
        private Cookie() {
            throw new IllegalStateException("Constants class");
        }
        
        public static final String REFRESH_TOKEN_NAME = "refresh_token";
        public static final String PATH = "/auth/refresh";
        public static final int MAX_AGE = 200 * 24 * 60 * 60; // 200일
        public static final boolean HTTP_ONLY = true;
        public static final boolean SECURE = true;
        public static final String SAME_SITE = "Strict";
    }

    public static final class Token {
        private Token() {
            throw new IllegalStateException("Constants class");
        }
        
        public static final String BEARER_TYPE = "Bearer";
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60; // 30분 (초 단위)
        public static final long REFRESH_TOKEN_EXPIRE_TIME = 200 * 24 * 60 * 60L; // 200일 (초 단위)
    }
}