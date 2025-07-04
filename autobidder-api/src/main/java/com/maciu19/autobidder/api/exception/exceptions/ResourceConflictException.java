package com.maciu19.autobidder.api.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceConflictException extends RuntimeException {
    
    public ResourceConflictException(String message) {
        super(message);
    }
}
