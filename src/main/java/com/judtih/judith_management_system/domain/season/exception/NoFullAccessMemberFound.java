package com.judtih.judith_management_system.domain.season.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

public class NoFullAccessMemberFound extends BusinessException {
    public NoFullAccessMemberFound(String message, int status, String error) {
        super(message, status, error);
    }

    public NoFullAccessMemberFound(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
