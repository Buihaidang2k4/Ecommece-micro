package com.myshop.auth.exception;

import com.myshop.commons.exception.ErrorCodeSpec;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ErrorCodeSpec {
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

    AuthErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
