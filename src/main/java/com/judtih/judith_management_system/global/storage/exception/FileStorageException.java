package com.judtih.judith_management_system.global.storage.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when a file upload fails due to an IO error or missing filename, in both local and S3 storage. */
public class FileStorageException extends BusinessException {

    public FileStorageException(String message, int status, String error) {
        super(message, status, error);
    }

    public FileStorageException(String message, int status, String error, Throwable cause) {
        super(message, status, error, cause);
    }
}
