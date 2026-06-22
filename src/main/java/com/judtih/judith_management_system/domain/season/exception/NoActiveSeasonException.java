package com.judtih.judith_management_system.domain.season.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when an operation requires an ACTIVE season but none exists. */
public class NoActiveSeasonException extends BusinessException {

    public NoActiveSeasonException(String message, int status, String error) {
        super(message, status, error);
    }

    public NoActiveSeasonException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
