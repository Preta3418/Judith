package com.judtih.judith_management_system.shared.notification.exception;

import com.judtih.judith_management_system.shared.exception.BusinessException;

public class NoNotificationFoundException extends BusinessException {

    public NoNotificationFoundException(String message, int status, String error) {
        super(message, status, error);
    }

    public NoNotificationFoundException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
