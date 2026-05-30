package com.example.wseety.exceptionHandler.exception;


import org.springframework.http.HttpStatus;

public class ExternalServiceTimeoutException extends BaseException {

    public ExternalServiceTimeoutException(String message) {
        super(message, HttpStatus.GATEWAY_TIMEOUT, "EXTERNAL_SERVICE_TIMEOUT");
    }
}