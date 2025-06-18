package com.maciu19.autobidder.api.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.api.Http;
import com.maciu19.autobidder.api.exception.exceptions.*;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("An unexpected error occurred for request on path: {}", request.getRequestURI(), ex);
        String message = "An unexpected internal server error has occurred. Please contact support.";

        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR,
                message,
                request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDto handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                request.getRequestURI(),
                fieldErrors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponseDto handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String detailedMessage = "The request body is invalid or malformed.";

        Throwable cause = ex.getRootCause();
        if (cause instanceof InvalidFormatException ifx && ifx.getTargetType().isEnum()) {
            String fieldName = ifx.getPath().getLast().getFieldName();
            detailedMessage = String.format("Invalid value '%s' for field '%s'. Allowed values are: %s",
                    ifx.getValue(),
                    fieldName,
                    Arrays.toString(ifx.getTargetType().getEnumConstants()));
        }

        return new ErrorResponseDto(HttpStatus.BAD_REQUEST, detailedMessage, request.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request
    ) {
        return new ErrorResponseDto(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler({
            DuplicateResourceException.class,
            ResourceConflictException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleConflictResourceException(
            Exception ex, HttpServletRequest request
    ) {
        return new ErrorResponseDto(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(ForbiddenResourceException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleForbiddenResourceException(
            ForbiddenResourceException ex, HttpServletRequest request
    ) {
        return new ErrorResponseDto(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(FileUploadFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleFileUploadFailed(FileUploadFailedException ex, HttpServletRequest request) {
        log.error("File upload failed for request on path: {}", request.getRequestURI(), ex);
        String message = "An unexpected error occurred during file upload. Please try again later.";

        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR,
                message,
                request.getRequestURI());
    }
}
