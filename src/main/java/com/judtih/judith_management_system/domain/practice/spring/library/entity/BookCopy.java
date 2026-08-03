package com.judtih.judith_management_system.domain.practice.spring.library.entity;

import com.judtih.judith_management_system.domain.practice.spring.library.enums.BookCondition;
import com.judtih.judith_management_system.domain.practice.spring.library.enums.BookStatus;

/**
 * Physical copy of a book. A single book title may have several copies.
 * Status transitions (called by service methods, never by callers directly):
 *   AVAILABLE → BORROWED  (via markBorrowed on borrow)
 *   BORROWED → AVAILABLE  (via markAvailable on return, if condition OK)
 *   BORROWED → LOST       (via markLost on return, if condition DAMAGED)
 *   AVAILABLE → RESERVED  (via markReserved when someone claims a hold — optional)
 */
public class BookCopy {

    private Long id;
    private Book book;                    // ManyToOne
    private BookStatus status;
    private BookCondition condition;

    public BookCopy() {}
    public BookCopy(Book book, BookCondition condition) {
        this.book = book;
        this.condition = condition;
        this.status = BookStatus.AVAILABLE;
    }

    public Long getId() { return id; }
    public Book getBook() { return book; }
    public BookStatus getStatus() { return status; }
    public BookCondition getCondition() { return condition; }

    public void markBorrowed()   { this.status = BookStatus.BORROWED; }
    public void markAvailable()  { this.status = BookStatus.AVAILABLE; }
    public void markReserved()   { this.status = BookStatus.RESERVED; }
    public void markLost()       { this.status = BookStatus.LOST; }
    public void updateCondition(BookCondition c) { this.condition = c; }
}
