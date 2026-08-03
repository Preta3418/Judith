package com.judtih.judith_management_system.domain.practice.spring.library.enums;

public enum LoanStatus {
    ACTIVE,     // currently checked out, not yet returned
    RETURNED,   // returned normally
    OVERDUE     // computed at query time; can also be stored if you'd rather
}
