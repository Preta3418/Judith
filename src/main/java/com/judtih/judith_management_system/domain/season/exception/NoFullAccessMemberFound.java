package com.judtih.judith_management_system.domain.season.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when activating a season that has no member with a full-access role (LEADER, PRODUCER, etc.). */
public class NoFullAccessMemberFound extends BusinessException {
    public NoFullAccessMemberFound(String message, int status, String error) {
        super(message, status, error);
    }

    public NoFullAccessMemberFound(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
