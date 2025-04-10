package com.annyang.global.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorResponse error;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.of(true, data, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.of(false, null, new ErrorResponse(errorCode));
    }
}