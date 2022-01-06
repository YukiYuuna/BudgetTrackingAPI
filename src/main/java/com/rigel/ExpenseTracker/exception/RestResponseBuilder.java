package com.rigel.ExpenseTracker.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

public class RestResponseBuilder {

    private int httpStatus;
    private String error;
    private String message;
    private String path;

    public RestResponseBuilder status(int httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }
    public RestResponseBuilder status(HttpStatus httpStatus) {
        this.httpStatus = httpStatus.value();
        if (httpStatus.isError()) {
            this.error = httpStatus.getReasonPhrase();
        }
        return this;
    }
    public RestResponseBuilder error(String error) {
        this.error = error;
        return this;
    }
    public RestResponseBuilder exception(ResponseStatusException exception) {
        HttpStatus status = exception.getStatus();
        this.httpStatus = status.value();

        if (!Objects.requireNonNull(exception.getReason()).isBlank()) {
            this.message = exception.getReason();
        }

        if (status.isError()) {
            this.error = status.getReasonPhrase();
        }
        return this;
    }
    public RestResponseBuilder message(String message) {
        this.message = message;
        return this;
    }
    public RestResponseBuilder path(String path) {
        this.path = path;
        return this;
    }
    public RestApiException build() {
        RestApiException response = new RestApiException();
        response.setHttpStatus(httpStatus);
        response.setError(error);
        response.setMessage(message);
        response.setPath(path);
        return response;
    }
    public ResponseEntity<RestApiException> entity() {
        return ResponseEntity.status(httpStatus).headers(HttpHeaders.EMPTY).body(build());
    }
}
