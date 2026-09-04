package com.myshop.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1002, "Access denied", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1003, "Invalid token", HttpStatus.UNAUTHORIZED),
    RESOURCE_NOT_FOUND(1004, "Resource not found", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR(1005, "Validation failed", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(1999, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    TOKEN_EXPIRED(1006, "Token expired", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(1007, "Token has been revoked or is no longer valid", HttpStatus.UNAUTHORIZED),

    USER_EXISTED(1100, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1101, "User not found", HttpStatus.NOT_FOUND),
    USER_INVALID(1102, "Invalid user", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1104, "Incorrect email or password", HttpStatus.UNAUTHORIZED),
    PASSWORD_NOT_MATCHES(1105, "Current password is incorrect", HttpStatus.BAD_REQUEST),
    USER_ALREADY_LOCKED(1107, "This user account is already locked", HttpStatus.BAD_REQUEST),

    OTP_INVALID(2401, "OTP is invalid", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
