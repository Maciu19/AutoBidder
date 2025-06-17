package com.maciu19.autobidder.api.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenResourceException extends RuntimeException {

    public ForbiddenResourceException(String message) {
        super(message);
    }
}
