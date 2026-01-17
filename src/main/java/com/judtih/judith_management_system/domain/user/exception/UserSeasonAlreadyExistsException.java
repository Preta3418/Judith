package com.judtih.judith_management_system.domain.user.exception;

import com.judtih.judith_management_system.shared.exception.BusinessException;

public class UserSeasonAlreadyExistsException extends BusinessException {
    public UserSeasonAlreadyExistsException(String message, int status, String error) {
        super(message, status, error);
    }

    public UserSeasonAlreadyExistsException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
