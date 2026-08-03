package com.judtih.judith_management_system.domain.practice.spring.library.exception;

/** 409 — member has at least one overdue loan; must return before doing anything new. */
public class OverdueBlockException extends RuntimeException {
    public OverdueBlockException(Long memberId) { super("Member has overdue loans: " + memberId); }
}
