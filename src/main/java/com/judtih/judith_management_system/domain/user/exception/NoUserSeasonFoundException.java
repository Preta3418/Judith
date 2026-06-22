package com.judtih.judith_management_system.domain.user.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when no UserSeason record is found for the given userId + seasonId combination. */
public class NoUserSeasonFoundException extends BusinessException {
    public NoUserSeasonFoundException(String message, int status, String error) {
        super(message, status, error);
    }

    public NoUserSeasonFoundException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
