package com.myshop.file.exception;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        String message = e.getMessage() != null ? e.getMessage() : errorCode.getMessage();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.of(errorCode.getCode(), message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.of(errorCode.getCode(), e.getMessage(), null));
    }
}
