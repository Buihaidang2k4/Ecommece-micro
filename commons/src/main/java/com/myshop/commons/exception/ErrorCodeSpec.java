package com.myshop.commons.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCodeSpec {
    int getCode();

    String getMessage();

    HttpStatus getHttpStatus();
}
