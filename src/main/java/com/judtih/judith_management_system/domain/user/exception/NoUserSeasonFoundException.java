package com.judtih.judith_management_system.domain.user.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

public class NoUserSeasonFoundException extends BusinessException {
    public NoUserSeasonFoundException(String message, int status, String error) {
        super(message, status, error);
    }

    public NoUserSeasonFoundException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
