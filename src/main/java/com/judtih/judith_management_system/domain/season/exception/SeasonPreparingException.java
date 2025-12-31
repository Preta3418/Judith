package com.judtih.judith_management_system.domain.season.exception;

import com.judtih.judith_management_system.shared.exception.BusinessException;

public class SeasonPreparingException extends BusinessException {
    public SeasonPreparingException(String message, int status, String error) {
        super(message, status, error);
    }

    public SeasonPreparingException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
