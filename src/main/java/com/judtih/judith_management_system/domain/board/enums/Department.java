package com.judtih.judith_management_system.domain.board.enums;

import com.judtih.judith_management_system.domain.user.enums.UserRole;

import java.util.Collections;
import java.util.Set;

/**
 * The four department boards. Every season member can VIEW every board;
 * posting is gated by {@link #canPost(Set)}.
 *
 * PLANNING is intentionally NOT here — the planning system is a separate
 * Phase 4 domain living under the admin tab group, not a department board.
 */
public enum Department {

    STAGE_DESIGN("무대 디자인", Set.of(UserRole.STAGE_DESIGN)),
    SOUND_DESIGN("음향 디자인", Set.of(UserRole.SOUND_DESIGN, UserRole.SOUND_OPERATOR)),
    PRINT_DESIGN("인쇄/홍보", Set.of(UserRole.IMAGE_DESIGN)),
    PROP_DESIGN("소품", Set.of());  // empty targetRoles = every season member can post

    private final String label;
    private final Set<UserRole> targetRoles;

    Department(String label, Set<UserRole> targetRoles) {
        this.label = label;
        this.targetRoles = targetRoles;
    }

    public String getLabel() {
        return label;
    }

    public Set<UserRole> getTargetRoles() {
        return targetRoles;
    }

    /**
     * Single source of truth for "can this member post here?".
     * Order matters:
     *  1. Full-access roles (LEADER/PRODUCER/SUB_PRODUCER/PLANNER) can post anywhere.
     *  2. Empty targetRoles means the board is open to every season member (PROP_DESIGN).
     *  3. Otherwise the member needs at least one of this department's target roles.
     *
     * Note: the SUPER ADMIN has no UserSeason rows, so their role set is empty and
     * this method alone would reject them. BoardService handles that case with the
     * hasFullAccess flag from the controller — this method only covers season members.
     */
    public boolean canPost(Set<UserRole> memberRoles) {
        if (UserRole.hasFullAccess(memberRoles)) return true;
        if (targetRoles.isEmpty()) return true;
        return memberRoles != null && !Collections.disjoint(memberRoles, targetRoles);
    }
}
