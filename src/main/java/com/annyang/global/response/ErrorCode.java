package com.annyang.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INTERNAL_SERVER_ERROR("G001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT("G002", "Invalid input value", HttpStatus.BAD_REQUEST),

    // 인증
    UNAUTHORIZED("A001", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("A002", "Token has expired", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("A003", "Access is forbidden", HttpStatus.FORBIDDEN),

    // 회원
    MEMBER_NOT_FOUND("M001", "Member not found", HttpStatus.NOT_FOUND),
    DUPLICATED_EMAIL("M002", "Email already exists", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}