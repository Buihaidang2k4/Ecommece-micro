package com.myshop.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode implements ErrorCodeSpec {
    UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1002, "Access denied", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND(1004, "Resource not found", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR(1005, "Validation failed", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(1999, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    TOKEN_EXPIRED(1006, "Token expired", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(1007, "Token has been revoked or is no longer valid", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
