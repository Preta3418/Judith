package com.judtih.judith_management_system.domain.season.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when a requested season does not exist or cannot be found by the given criteria. */
public class NoSeasonFoundException extends BusinessException {

    public NoSeasonFoundException(String message, int status, String error) {
        super(message, status, error);
    }

    public NoSeasonFoundException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
