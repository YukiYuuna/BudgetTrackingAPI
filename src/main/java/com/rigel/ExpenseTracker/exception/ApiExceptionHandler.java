package com.rigel.ExpenseTracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class}) //declaring the thrown exception
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {

//        Creating a payload for the exception details:
        HttpStatus badRequest = HttpStatus.NOT_FOUND;
        RestApiException exception = new RestApiException(badRequest, LocalDateTime.now(),e.getMessage());

//        returning the response entity for the exception
        return new ResponseEntity<>(exception, badRequest);
    }

    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(BadRequestException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        RestApiException exception = new RestApiException(badRequest, LocalDateTime.now(), e.getMessage());
        return new ResponseEntity<>(exception, badRequest);
    }
}
