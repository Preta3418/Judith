package com.judtih.judith_management_system.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Request body for PUT /api/password; currentPassword is verified before the new password is accepted. */
@Getter
@AllArgsConstructor
public class PasswordChangeRequest {
    String currentPassword;
    String newPassword;
}
