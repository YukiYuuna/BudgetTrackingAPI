package com.rigel.ExpenseTracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class RestExceptionHandler {

    @ExceptionHandler(value = {RestApiException.class})
    public ResponseEntity<Object> handleRestException(RestApiException e) {
        String error = "Exception has occured.";
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        RestApiException restApiException = new RestApiException(badRequest, ZonedDateTime.now(),error, e.getThrowable());
        return new ResponseEntity<>(restApiException, badRequest);
    }

//    private ResponseEntity<Object> buildResponseEntity(RestApiException e) {
//        new RestApiException(
//                HttpStatus.NOT_FOUND, LocalDateTime.now(), e.getMessage(),
//        );
//    }
//
//    //other exception handlers below
//    @ExceptionHandler(EntityNotFoundException.class)
//    protected ResponseEntity<Object> handleEntityNotFound(
//            EntityNotFoundException ex) {
//        RestApiException apiError = new RestApiException(HttpStatus.NOT_FOUND);
//        apiError.setMessage(ex.getMessage());
//        return buildResponseEntity(apiError);
//    }

}
