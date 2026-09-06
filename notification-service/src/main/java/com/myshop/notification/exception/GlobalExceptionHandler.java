package com.myshop.notification.exception;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.ErrorCodeSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCodeSpec errorCode = e.getErrorCode();
        String message = e.getMessage() != null ? e.getMessage() : errorCode.getMessage();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.of(errorCode.getCode(), message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.of(errorCode.getCode(), errorCode.getMessage(), null));
    }
}
