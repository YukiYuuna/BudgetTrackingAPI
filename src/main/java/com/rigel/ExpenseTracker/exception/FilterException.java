package com.rigel.ExpenseTracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class FilterException extends RuntimeException {

    private HttpStatus status;
    private String message;
    private Object result;

    public FilterException(HttpStatus status, String message, Object result){
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public FilterException(String message){
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
