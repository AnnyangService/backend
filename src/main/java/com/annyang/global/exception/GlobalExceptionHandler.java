package com.annyang.global.exception;

import com.annyang.global.response.ApiResponse;
import com.annyang.global.response.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 애플리케이션 전체의 예외를 처리하는 글로벌 예외 핸들러
 * 모든 예외는 일관된 응답 형식(ApiResponse)으로 변환됩니다.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 모든 예외 응답을 생성하는 유틸리티 메서드
     * ErrorCode에 정의된 정보를 바탕으로 일관된 형식의 에러 응답을 생성합니다.
     * 
     * @param errorCode 응답에 포함될 에러 코드 정보
     * @return 에러 정보를 포함한 API 응답
     */
    private ResponseEntity<ApiResponse<Void>> createErrorResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }

    /**
     * 비즈니스 예외 처리
     * 애플리케이션에서 의도적으로 발생시킨 예외들을 처리합니다.
     * 각 비즈니스 예외는 자신만의 ErrorCode를 가지고 있어 명확한 에러 메시지를 제공합니다.
     *
     * @param e 발생한 비즈니스 예외
     * @return 비즈니스 예외의 ErrorCode 정보를 포함한 응답
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.debug("Business exception occurred: {}", e.getMessage());
        return createErrorResponse(e.getErrorCode());
    }
    
    /**
     * 입력값 검증 예외 처리
     * 클라이언트 요청의 데이터 검증 실패 시 발생하는 예외를 처리합니다.
     * - MethodArgumentNotValidException: @RequestBody와 @Valid 사용 시 발생 (주로 JSON 요청 검증)
     * - BindException: @ModelAttribute와 @Valid 사용 시 발생 (주로 폼 데이터, 쿼리 파라미터 검증)
     * - IllegalArgumentException: 코드 내부에서 검증 실패 시 발생
     *
     * @param e 발생한 검증 예외
     * @return INVALID_INPUT 에러 코드를 포함한 응답
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(Exception e) {
        log.debug("Validation exception occurred: {}", e.getMessage());
        return createErrorResponse(ErrorCode.INVALID_INPUT);
    }
    
    /**
     * 인증 실패 예외 처리
     * 잘못된 자격 증명(이메일, 비밀번호 등)으로 인한 인증 실패 시 발생하는 예외를 처리합니다.
     *
     * @param e 발생한 인증 실패 예외
     * @return UNAUTHORIZED 에러 코드를 포함한 응답
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        log.debug("Authentication exception occurred: {}", e.getMessage());
        return createErrorResponse(ErrorCode.UNAUTHORIZED);
    }
    
    /**
     * 접근 권한 예외 처리
     * 인증은 되었으나 요청한 리소스에 접근할 권한이 없을 때 발생하는 예외를 처리합니다.
     *
     * @param e 발생한 접근 권한 예외
     * @return FORBIDDEN 에러 코드를 포함한 응답
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.debug("Access denied exception occurred: {}", e.getMessage());
        return createErrorResponse(ErrorCode.FORBIDDEN);
    }
    
    /**
     * 처리되지 않은 모든 예외 처리
     * 위의 특정 예외 처리기에 해당하지 않는 모든 예외를 처리합니다.
     * 예상치 못한 서버 오류나 명시적으로 처리되지 않은 예외를 포착합니다.
     *
     * @param e 발생한 처리되지 않은 예외
     * @return INTERNAL_SERVER_ERROR 에러 코드를 포함한 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllUncaughtException(Exception e) {
        log.warn("Unhandled exception occurred: {}", e.getMessage(), e);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }
} 