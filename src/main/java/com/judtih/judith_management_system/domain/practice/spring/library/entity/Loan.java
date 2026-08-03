package com.judtih.judith_management_system.domain.practice.spring.library.entity;

import com.judtih.judith_management_system.domain.practice.spring.library.enums.LoanStatus;

import java.time.LocalDateTime;

/**
 * Checkout record — one member borrowing one BookCopy.
 * When returnedAt is null, the loan is ACTIVE.
 * A loan is OVERDUE when today > dueAt AND returnedAt is null.
 */
public class Loan {

    public static final int DEFAULT_LOAN_DAYS = 14;
    public static final int EXTENSION_DAYS = 7;
    public static final long FINE_PER_DAY = 100L;

    private Long id;
    private Member member;
    private BookCopy bookCopy;
    private LocalDateTime borrowedAt;
    private LocalDateTime dueAt;
    private LocalDateTime returnedAt;   // nullable
    private LoanStatus status;
    private boolean extended;           // one extension allowed

    public Loan() {}
    public Loan(Member member, BookCopy bookCopy) {
        this.member = member;
        this.bookCopy = bookCopy;
        this.borrowedAt = LocalDateTime.now();
        this.dueAt = borrowedAt.plusDays(DEFAULT_LOAN_DAYS);
        this.status = LoanStatus.ACTIVE;
        this.extended = false;
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public BookCopy getBookCopy() { return bookCopy; }
    public LocalDateTime getBorrowedAt() { return borrowedAt; }
    public LocalDateTime getDueAt() { return dueAt; }
    public LocalDateTime getReturnedAt() { return returnedAt; }
    public LoanStatus getStatus() { return status; }
    public boolean isExtended() { return extended; }

    /** True if the loan is still active and past its due date. */
    public boolean isOverdue() {
        return returnedAt == null && LocalDateTime.now().isAfter(dueAt);
    }

    public void markReturned() {
        this.returnedAt = LocalDateTime.now();
        this.status = LoanStatus.RETURNED;
    }

    public void extend() {
        this.dueAt = this.dueAt.plusDays(EXTENSION_DAYS);
        this.extended = true;
    }
}
