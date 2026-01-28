package com.judtih.judith_management_system.global.security.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

public class WrongUsernamePasswordException extends BusinessException {
    public WrongUsernamePasswordException(String message, int status, String error) {
        super(message, status, error);
    }

    public WrongUsernamePasswordException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
