package com.judtih.judith_management_system.domain.practice.spring.library.enums;

/** State of a physical book copy. */
public enum BookStatus {
    AVAILABLE,   // sitting on the shelf, can be borrowed
    BORROWED,    // currently checked out to a member
    RESERVED,    // held for the first waitlist member (short window)
    LOST         // reported lost or damaged beyond use
}
