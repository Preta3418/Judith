package com.judtih.judith_management_system.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/** Request body for creating or updating a user account; studentNumber doubles as the initial password. */
@Getter
@NoArgsConstructor
public class UserRequest {
    private String name;
    private String studentNumber;
    private String phoneNumber;
    private boolean isAdmin;
}
