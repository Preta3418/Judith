package com.judtih.judith_management_system.domain.season.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when a write operation is attempted on a CLOSED season (seasons are fully read-only once closed). */
public class SeasonClosedException extends BusinessException {

    public SeasonClosedException(String message, int status, String error) {
        super(message, status, error);
    }

    public SeasonClosedException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
