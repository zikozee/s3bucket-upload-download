package com.phyna.uploaddocument.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileRuntimeException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public FileRuntimeException(String message) {
        super(message);
    }

    public FileRuntimeException() {
        super();
    }

    public FileRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileRuntimeException(Throwable cause) {
        super(cause);
    }

    protected FileRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
