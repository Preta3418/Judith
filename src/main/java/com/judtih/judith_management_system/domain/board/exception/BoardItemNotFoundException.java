package com.judtih.judith_management_system.domain.board.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/** Thrown when a post, comment, folder, or attachment does not exist (404). */
public class BoardItemNotFoundException extends BusinessException {

    public BoardItemNotFoundException(String message) {
        super(message, 404, "Not Found");
    }
}
