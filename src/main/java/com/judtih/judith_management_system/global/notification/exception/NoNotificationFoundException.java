package com.judtih.judith_management_system.global.notification.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when a UserNotification lookup by ID finds no matching record. */
public class NoNotificationFoundException extends BusinessException {

    public NoNotificationFoundException(String message, int status, String error) {
        super(message, status, error);
    }

    public NoNotificationFoundException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
