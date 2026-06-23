package com.judtih.judith_management_system.global.exception;

import lombok.Getter;

/** Base exception for all domain-level errors; carries HTTP status and error string for structured error responses. */
@Getter
public class BusinessException extends RuntimeException {

    private final int status;
    private final String error;

    public BusinessException(String message, int status, String error) {
        super(message);
        this.status = status;
        this.error = error;

    }

    public BusinessException(String message, int status, String error, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.error = error;
    }


}
