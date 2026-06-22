package com.judtih.judith_management_system.domain.user.enums;

import java.util.Collections;
import java.util.Set;

/**
 * Roles a member can hold within a season; the first four are full-access roles with elevated privileges.
 * Full-access roles can create shared calendar events, send notifications, and see all scripts.
 */
public enum UserRole {
    // Full-access roles (학회장, 연출, 조연출, 기획)
    LEADER, PRODUCER, SUB_PRODUCER, PLANNER,

    // General production roles
    ACTOR, STAFF,
    SOUND_OPERATOR, LIGHT_OPERATOR,
    SOUND_DESIGN, LIGHT_DESIGN,
    IMAGE_DESIGN, STAGE_DESIGN;

    // Defined once and reused by hasFullAccess() to avoid repeated Set construction
    private static final Set<UserRole> FULL_ACCESS_ROLES = Set.of(
            UserRole.LEADER, UserRole.PRODUCER, UserRole.SUB_PRODUCER, UserRole.PLANNER
    );

    /** Returns true if the given role set contains at least one full-access role; null-safe. */
    public static boolean hasFullAccess(Set<UserRole> roles) {
        return roles != null && !Collections.disjoint(roles, FULL_ACCESS_ROLES);
    }
}
