package com.judtih.judith_management_system.domain.season.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when trying to activate a season while another season is already ACTIVE. */
public class AlreadyActiveSeasonException extends BusinessException {

    public AlreadyActiveSeasonException(String message, int status, String error) {
        super(message, status, error);
    }

    public AlreadyActiveSeasonException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
