package com.maciu19.autobidder.api.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileUploadFailedException extends RuntimeException {

    public FileUploadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
