package com.judtih.judith_management_system.domain.practice.spring.library.exception;

/** 403 — deactivated members cannot borrow, reserve, or extend. */
public class MemberInactiveException extends RuntimeException {
    public MemberInactiveException(Long memberId) { super("Member is inactive: " + memberId); }
}
