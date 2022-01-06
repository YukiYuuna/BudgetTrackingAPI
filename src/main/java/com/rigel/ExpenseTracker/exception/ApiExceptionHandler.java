package com.rigel.ExpenseTracker.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<?> handleStatusException(ResponseStatusException ex, WebRequest request) {
        log.error(ex.getReason(), ex);
        return RestApiException.builder()
                .exception(ex)
                .path(request.getDescription(false).substring(4))
                .entity();
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        RestApiException restApiException = new RestApiException(status.value(), status.getReasonPhrase(), ex.getMessage(), getPath(request));
        return ResponseEntity.badRequest().body(restApiException);
    }


    @SuppressWarnings("unchecked")
    protected @Override ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                                       HttpStatus status, WebRequest request) {
        ResponseEntity<?> responseEntity;
        if (!status.isError()) {
            responseEntity = handleStatusException(ex, status, request);
        } else if (INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
            responseEntity = handleEveryException(ex, request);
        } else {
            responseEntity = handleEveryException(ex, request);
        }
        return (ResponseEntity<Object>) responseEntity;
    }

    protected ResponseEntity<RestApiException> handleStatusException(Exception ex, HttpStatus status, WebRequest request) {
        return RestApiException.builder()
                .status(status)
                .message("Execution halted")
                .path(getPath(request))
                .entity();
    }

//    Handling All unhandled exception:
    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
        logger.error(ex.getLocalizedMessage(), ex);
        return handleEveryException(ex, request);
    }

    protected ResponseEntity<RestApiException> handleEveryException(Exception ex, WebRequest request) {
        return RestApiException.builder()
                .status(INTERNAL_SERVER_ERROR)
                .message("Server encountered an error")
                .path(getPath(request))
                .entity();
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).substring(4);
    }

//    @ExceptionHandler(value = {NotFoundException.class}) //declaring the thrown exception
//    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
//
////        Creating a payload for the exception details:
//        HttpStatus notFound = HttpStatus.NOT_FOUND;
//        RestApiException exception = new RestApiException(notFound, LocalDateTime.now(),e.getMessage());
//
////        returning the response entity for the exception
//        return new ResponseEntity<>(exception, notFound);
//    }
//
//    @ExceptionHandler(value = {BadRequestException.class})
//    public ResponseEntity<Object> handleBadRequestException(BadRequestException e) {
//        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
//        RestApiException exception = new RestApiException(badRequest, LocalDateTime.now(), e.getMessage());
//        return new ResponseEntity<>(exception, badRequest);
//    }
//
//    @ExceptionHandler(value = {NotValidUrlException.class})
//    public ResponseEntity<Object> handleNotValidUrlException(NotValidUrlException e) {
//        return notAllowedException(e);
//    }
//
//    @ExceptionHandler(value = {NotAllowedException.class})
//    public ResponseEntity<Object> handleNotAllowedException(NotAllowedException e) {
//        return notAllowedException(e);
//    }
//
////    @ExceptionHandler(AuthenticationException.class)
////    public FilterException handleAuthenticationException(AuthenticationException ex, HttpServletResponse response){
////        FilterException filterException = new FilterException(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
////        response.setStatus(401);
////        return filterException;
////    }
//
//    private ResponseEntity<Object> notAllowedException(RuntimeException e){
//        HttpStatus notValidUrl = HttpStatus.NOT_ACCEPTABLE;
//        RestApiException exception = new RestApiException(notValidUrl, LocalDateTime.now(), e.getMessage());
//        return new ResponseEntity<>(exception, notValidUrl);
//    }
}
