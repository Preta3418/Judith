package com.judtih.judith_management_system.domain.season.dto;

import com.judtih.judith_management_system.domain.user.enums.UserRole;
import lombok.Getter;

import java.util.Set;

/** Inline member entry within SeasonRequest used when creating a season with its initial cast. */
@Getter
public class SeasonMemberRequest {
    private Long userId;
    private Set<UserRole> roles;
}
