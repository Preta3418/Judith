package com.judtih.judith_management_system.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessExceptionHandler (BusinessException e, HttpServletRequest request) {

        ErrorResponse response = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(e.getStatus())
                .message(e.getMessage())
                .error(e.getError())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(e.getStatus()).body(response);
    }



}
