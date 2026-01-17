package com.judtih.judith_management_system.domain.user.dto;

import com.judtih.judith_management_system.domain.user.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSeasonResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long seasonId;
    private String seasonName;
    private Set<UserRole> roles;
    private LocalDateTime joinedAt;

}
