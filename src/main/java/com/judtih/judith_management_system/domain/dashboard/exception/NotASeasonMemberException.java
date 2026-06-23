package com.judtih.judith_management_system.domain.dashboard.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when a user tries to access dashboard data for a season they are not a member of. */
public class NotASeasonMemberException extends BusinessException {

    public NotASeasonMemberException(String message) {
        super(message, 403, "Forbidden");
    }
}
