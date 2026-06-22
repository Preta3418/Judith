package com.judtih.judith_management_system.domain.user.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when a user lookup by ID or student number finds no matching record. */
public class NoUserFoundException extends BusinessException {
    public NoUserFoundException(String message, int status, String error) {
        super(message, status, error);
    }

    public NoUserFoundException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
