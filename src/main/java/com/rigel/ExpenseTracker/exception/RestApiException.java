package com.rigel.ExpenseTracker.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class RestApiException {

    private final HttpStatus httpStatus;
    private final LocalDateTime timestamp;
    private final String message;

    public RestApiException(HttpStatus httpStatus, LocalDateTime timestamp, String message) {
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
