package com.judtih.judith_management_system.domain.user.enums;

/**
 * Membership status of a user; INACTIVE users are treated as alumni and are excluded from season operations
 * but are targeted by the SMS broadcast feature.
 */
public enum UserStatus {
    ACTIVE,   // current club member
    INACTIVE  // alumni / left the club
}