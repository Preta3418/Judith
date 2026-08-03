package com.judtih.judith_management_system.domain.practice.spring.library.exception;

/** 409 — member already has as many concurrent loans as their tier permits. */
public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(int limit) { super("Loan limit reached: " + limit); }
}
