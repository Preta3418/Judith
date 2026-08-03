package com.judtih.judith_management_system.domain.board.enums;

import com.judtih.judith_management_system.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/** Pure unit tests for the posting-permission rule — the single access gate of the board. */
public class DepartmentTest {

    @Test
    void fullAccessRole_canPostAnywhere() {
        Set<UserRole> leader = Set.of(UserRole.LEADER);
        for (Department d : Department.values()) {
            assertThat(d.canPost(leader)).isTrue();
        }
    }

    @Test
    void matchingRole_canPostToOwnDepartment() {
        assertThat(Department.STAGE_DESIGN.canPost(Set.of(UserRole.STAGE_DESIGN))).isTrue();
        assertThat(Department.PRINT_DESIGN.canPost(Set.of(UserRole.IMAGE_DESIGN))).isTrue();
    }

    @Test
    void soundDepartment_acceptsBothSoundRoles() {
        assertThat(Department.SOUND_DESIGN.canPost(Set.of(UserRole.SOUND_DESIGN))).isTrue();
        assertThat(Department.SOUND_DESIGN.canPost(Set.of(UserRole.SOUND_OPERATOR))).isTrue();
    }

    @Test
    void nonMatchingRole_cannotPost() {
        assertThat(Department.STAGE_DESIGN.canPost(Set.of(UserRole.ACTOR))).isFalse();
        assertThat(Department.SOUND_DESIGN.canPost(Set.of(UserRole.STAGE_DESIGN))).isFalse();
    }

    @Test
    void propDesign_isOpenToEveryMember() {
        assertThat(Department.PROP_DESIGN.canPost(Set.of(UserRole.ACTOR))).isTrue();
        assertThat(Department.PROP_DESIGN.canPost(Set.of())).isTrue();
    }

    @Test
    void emptyRoles_cannotPostToRestrictedDepartment() {
        assertThat(Department.STAGE_DESIGN.canPost(Set.of())).isFalse();
    }

    @Test
    void nullRoles_handledSafely() {
        assertThat(Department.STAGE_DESIGN.canPost(null)).isFalse();
        assertThat(Department.PROP_DESIGN.canPost(null)).isTrue(); // open board short-circuits before the null check matters
    }
}
