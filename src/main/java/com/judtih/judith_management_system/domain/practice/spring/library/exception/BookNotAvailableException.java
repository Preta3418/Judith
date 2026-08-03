package com.judtih.judith_management_system.domain.practice.spring.library.exception;

/** 409 — no AVAILABLE copies for this book (member should reserve instead). */
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(Long bookId) { super("No available copies for book " + bookId); }
}
