package com.judtih.judith_management_system.domain.season.exception;

import com.judtih.judith_management_system.shared.exception.BusinessException;

public class NoSeasonFoundException extends BusinessException {

    public NoSeasonFoundException(String message, int status, String error) {
        super(message, status, error);
    }

    public NoSeasonFoundException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
