package com.judtih.judith_management_system.domain.dashboard.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

public class NotASeasonMemberException extends BusinessException {

    public NotASeasonMemberException(String message) {
        super(message, 403, "Forbidden");
    }
}
