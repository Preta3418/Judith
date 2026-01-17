package com.judtih.judith_management_system.domain.user.dto;

import com.judtih.judith_management_system.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRolesRequest {

    private Long userId;
    private Long seasonId;
    private Set<UserRole> userRoles;
}
