package com.maciu19.autobidder.api.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.maciu19.autobidder.api.exceptions.exceptions.DuplicateResourceException;
import com.maciu19.autobidder.api.exceptions.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleDuplicateResourceException(
            DuplicateResourceException ex, HttpServletRequest request
    ) {
        return new ErrorResponseDto(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI());
    }
}
