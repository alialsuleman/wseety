package com.example.wseety.exceptionHandler.exception;

import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends BaseException {

    public TooManyRequestsException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, "QUOTA_EXCEEDED");
    }

    public TooManyRequestsException(String message, Object additionalData) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, "QUOTA_EXCEEDED", additionalData);
    }
}