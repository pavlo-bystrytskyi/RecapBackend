package org.example.recapbackend.exception;

import org.example.recapbackend.dto.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String GENERIC_EXCEPTION_MESSAGE = "Something went wrong.";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleNoSuchElementException(NoSuchElementException exception) {
        return new ResponseError(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseError handleGlobalException(Exception exception) {
        return new ResponseError(GENERIC_EXCEPTION_MESSAGE);
    }
}
