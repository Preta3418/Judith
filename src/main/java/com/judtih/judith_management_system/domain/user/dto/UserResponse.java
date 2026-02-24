package com.judtih.judith_management_system.domain.user.dto;

import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String studentNumber;
    private String phoneNumber;
    private boolean isAdmin;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime inactiveSince;
}
