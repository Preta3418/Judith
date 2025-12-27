package com.judtih.judith_management_system.shared.upload.exception;

import com.judtih.judith_management_system.shared.exception.BusinessException;

public class FileStorageException extends BusinessException {

    public FileStorageException(String message, int status, String error) {
        super(message, status, error);
    }

    public FileStorageException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
