package com.judtih.judith_management_system.shared.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int status;
    private final String error;

    public BusinessException(String message, int status, String error) {
        super(message);
        this.status = status;
        this.error = error;

    }

    public BusinessException(String message, int status, String error, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.error = error;
    }


}
