package com.maciu19.autobidder.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto (
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {
    public ErrorResponseDto(HttpStatus status, String message, String path) {
        this(Instant.now(), status.value(), status.getReasonPhrase(), message, path, null);
    }

    public ErrorResponseDto(HttpStatus status, String message, String path, Map<String, String> validationErrors) {
        this(Instant.now(), status.value(), status.getReasonPhrase(), message, path, validationErrors);
    }
}
