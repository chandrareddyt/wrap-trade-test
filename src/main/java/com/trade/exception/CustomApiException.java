package com.trade.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class CustomApiException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public CustomApiException(HttpStatusCode statusCode, String message) {
        super(message);
        this.status = HttpStatus.valueOf(statusCode.value());
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
