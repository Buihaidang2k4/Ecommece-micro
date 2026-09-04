package com.example.MyShop_API.exception;

import com.example.MyShop_API.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.security.access.AccessDeniedException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    //     Custom
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessageTemplate());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    //     Bat moi loai loi chua duoc xu li
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error(e.getMessage(), e);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessageTemplate());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access denied: {}", e.getMessage());

        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessageTemplate())
                        .build());
    }

    /**
     * validate
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; ")); // nối tất cả lỗi bằng dấu ;

        log.warn("Validation failed: {}", message);

        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(ErrorCode.VALIDATION_ERROR.getCode())
                .message(message)
                .build());
    }


    /**
     * Json sai dinh dang
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleJsonParseError(HttpMessageNotReadableException e) {
        log.warn("Malformed JSON: {}", e.getMessage());

        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(ErrorCode.VALIDATION_ERROR.getCode())
                .message("Malformed JSON or invalid request format")
                .build());
    }

    /**
     * MissingServletRequestParameterException
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingParam(MissingServletRequestParameterException e) {
        String message = "Missing parameter: " + e.getParameterName();
        log.warn(message);

        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(ErrorCode.VALIDATION_ERROR.getCode())
                .message(message)
                .build());
    }
}
