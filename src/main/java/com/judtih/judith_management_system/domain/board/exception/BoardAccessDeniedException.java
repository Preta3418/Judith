package com.judtih.judith_management_system.domain.board.exception;

import com.judtih.judith_management_system.global.exception.BusinessException;

/**
 * Thrown when a member tries a board action they're not allowed to do (403):
 * posting without the department role, editing someone else's post, pinning
 * without full access, etc.
 */
public class BoardAccessDeniedException extends BusinessException {

    public BoardAccessDeniedException(String message) {
        super(message, 403, "Forbidden");
    }
}
