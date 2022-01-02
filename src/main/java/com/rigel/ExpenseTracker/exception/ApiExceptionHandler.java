package com.rigel.ExpenseTracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class}) //declaring the thrown exception
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {

//        Creating a payload for the exception details:
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        RestApiException exception = new RestApiException(notFound, LocalDateTime.now(),e.getMessage());

//        returning the response entity for the exception
        return new ResponseEntity<>(exception, notFound);
    }

    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(BadRequestException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        RestApiException exception = new RestApiException(badRequest, LocalDateTime.now(), e.getMessage());
        return new ResponseEntity<>(exception, badRequest);
    }

    @ExceptionHandler(value = {NotValidUrlException.class})
    public ResponseEntity<Object> handleNotValidUrlException(NotValidUrlException e) {
        return notAllowedException(e);
    }

    @ExceptionHandler(value = {NotAllowedException.class})
    public ResponseEntity<Object> handleNotAllowedException(NotAllowedException e) {
        return notAllowedException(e);
    }

//    @ExceptionHandler(AuthenticationException.class)
//    public FilterException handleAuthenticationException(AuthenticationException ex, HttpServletResponse response){
//        FilterException filterException = new FilterException(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
//        response.setStatus(401);
//        return filterException;
//    }

    private ResponseEntity<Object> notAllowedException(RuntimeException e){
        HttpStatus notValidUrl = HttpStatus.NOT_ACCEPTABLE;
        RestApiException exception = new RestApiException(notValidUrl, LocalDateTime.now(), e.getMessage());
        return new ResponseEntity<>(exception, notValidUrl);
    }
}
