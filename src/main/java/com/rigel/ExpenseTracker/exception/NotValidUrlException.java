package com.rigel.ExpenseTracker.exception;

public class NotValidUrlException extends RuntimeException{
    public NotValidUrlException() {
        super();
    }
    public NotValidUrlException(String message, Throwable cause) {
        super(message, cause);
    }
    public NotValidUrlException(String message) {
        super(message);
    }
    public NotValidUrlException(Throwable cause) {
        super(cause);
    }
}
