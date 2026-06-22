package com.judtih.judith_management_system.domain.season.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when a required field (e.g., eventDate) is missing on a season create/activate request. */
public class FieldNullException extends BusinessException {

    public FieldNullException(String message, int status, String error) {
        super(message, status, error);
    }

    public FieldNullException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
