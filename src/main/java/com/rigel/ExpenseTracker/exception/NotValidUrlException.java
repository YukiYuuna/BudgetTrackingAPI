package com.rigel.ExpenseTracker.exception;

public class NotValidUrlException extends RuntimeException{
    public NotValidUrlException(String message) {
        super(message);
    }
}
