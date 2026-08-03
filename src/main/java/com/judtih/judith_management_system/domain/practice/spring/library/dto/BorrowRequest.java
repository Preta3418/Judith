package com.judtih.judith_management_system.domain.practice.spring.library.dto;

/** POST body for /loans — member picks a book (service picks an available copy). */
public class BorrowRequest {
    public Long memberId;
    public Long bookId;
}
