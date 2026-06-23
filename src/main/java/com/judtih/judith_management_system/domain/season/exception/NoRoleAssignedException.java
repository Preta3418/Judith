package com.judtih.judith_management_system.domain.season.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when adding a member to an ACTIVE season without assigning any roles. */
public class NoRoleAssignedException extends BusinessException {
    public NoRoleAssignedException(String message, int status, String error) {
        super(message, status, error);
    }

    public NoRoleAssignedException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
