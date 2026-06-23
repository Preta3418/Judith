package com.judtih.judith_management_system.global.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** Structured JSON error body returned by GlobalExceptionHandler for all BusinessException subclasses. */
@Getter
@Builder
public class ErrorResponse {
    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
